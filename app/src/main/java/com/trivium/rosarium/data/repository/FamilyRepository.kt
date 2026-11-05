package com.trivium.rosarium.data.repository

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.trivium.rosarium.data.model.FamilyGroup
import com.trivium.rosarium.data.model.FamilyMember
import com.trivium.rosarium.data.model.InviteCodeGenerator
import com.trivium.rosarium.data.model.PendingInvite
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.util.UUID

private val Context.familyDataStore by preferencesDataStore(name = "rosarium_family_prefs")

class FamilyRepository(private val context: Context) {

    private val json = Json { ignoreUnknownKeys = true }
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    private val firestore: FirebaseFirestore? by lazy {
        try {
            FirebaseFirestore.getInstance()
        } catch (e: Exception) {
            null
        }
    }

    private val auth: FirebaseAuth? by lazy {
        try {
            FirebaseAuth.getInstance()
        } catch (e: Exception) {
            null
        }
    }

    private object Keys {
        val USER_ID = stringPreferencesKey("user_id")
        val USER_NAME = stringPreferencesKey("user_name")
        val ACTIVE_FAMILY_JSON = stringPreferencesKey("active_family_json")
        val PENDING_INVITE_CODE = stringPreferencesKey("pending_invite_code")
    }

    private val _currentFamily = MutableStateFlow<FamilyGroup?>(null)
    val currentFamily: StateFlow<FamilyGroup?> = _currentFamily.asStateFlow()

    private val _pendingInvite = MutableStateFlow<PendingInvite?>(null)
    val pendingInvite: StateFlow<PendingInvite?> = _pendingInvite.asStateFlow()

    private var cachedUserId: String = ""
    private var cachedUserName: String = "Family Pilgrim"

    init {
        scope.launch {
            // Ensure Anonymous Auth in background if Firebase is active
            ensureAuth()

            // Load user profile
            val prefs = context.familyDataStore.data.first()
            var uid = prefs[Keys.USER_ID]
            if (uid.isNullOrBlank()) {
                uid = auth?.currentUser?.uid ?: UUID.randomUUID().toString()
                context.familyDataStore.edit { it[Keys.USER_ID] = uid }
            }
            cachedUserId = uid
            cachedUserName = prefs[Keys.USER_NAME] ?: "Family Pilgrim"

            // Load saved local family
            val savedFamilyJson = prefs[Keys.ACTIVE_FAMILY_JSON]
            if (!savedFamilyJson.isNullOrBlank()) {
                try {
                    val group = json.decodeFromString<FamilyGroup>(savedFamilyJson)
                    _currentFamily.value = group
                    // Attempt background sync with Firestore
                    syncWithFirestore(group.id)
                } catch (e: Exception) {
                    // fallback
                }
            }
        }
    }

    private suspend fun ensureAuth() {
        withContext(Dispatchers.IO) {
            try {
                if (auth?.currentUser == null) {
                    auth?.signInAnonymously()?.await()
                }
            } catch (e: Exception) {
                // Non-blocking fallback
            }
        }
    }

    suspend fun getUserId(): String {
        if (cachedUserId.isNotBlank()) return cachedUserId
        val prefs = context.familyDataStore.data.first()
        val uid = prefs[Keys.USER_ID] ?: UUID.randomUUID().toString().also { newId ->
            context.familyDataStore.edit { it[Keys.USER_ID] = newId }
        }
        cachedUserId = uid
        return uid
    }

    suspend fun getUserName(): String {
        val prefs = context.familyDataStore.data.first()
        return prefs[Keys.USER_NAME] ?: cachedUserName
    }

    suspend fun setUserName(name: String) {
        cachedUserName = name
        context.familyDataStore.edit { it[Keys.USER_NAME] = name }
        // If part of a family, update member name in family
        _currentFamily.value?.let { current ->
            val updatedMembers = current.members.map { member ->
                if (member.id == cachedUserId) member.copy(name = name) else member
            }
            val updated = current.copy(members = updatedMembers)
            saveFamilyLocallyAndCloud(updated)
        }
    }

    /**
     * Creates a new family circle with an 8-character random hash code (e.g. "a8f9c2d1").
     */
    suspend fun createFamily(familyName: String, creatorName: String): FamilyGroup = withContext(Dispatchers.IO) {
        val uid = getUserId()
        val cleanName = creatorName.ifBlank { "Head of Family" }
        setUserName(cleanName)

        val inviteCode = InviteCodeGenerator.generateHash()
        val familyId = UUID.randomUUID().toString()

        val founder = FamilyMember(
            id = uid,
            name = cleanName,
            role = "Head of Family",
            joinedAt = System.currentTimeMillis(),
            rosariesPrayed = 0
        )

        val newFamily = FamilyGroup(
            id = familyId,
            name = familyName.trim().ifBlank { "Holy Family Circle" },
            inviteCode = inviteCode,
            creatorId = uid,
            creatorName = cleanName,
            createdAt = System.currentTimeMillis(),
            members = listOf(founder),
            familyIntention = "For peace, health, and grace in our family",
            totalRosaries = 0
        )

        saveFamilyLocallyAndCloud(newFamily)

        // Register invite code lookup in Firestore
        registerInviteCodeInFirestore(inviteCode, newFamily)

        newFamily
    }

    /**
     * Resolves an invite code to retrieve the pending invite preview details.
     */
    suspend fun resolveInvite(code: String): PendingInvite? = withContext(Dispatchers.IO) {
        val cleanCode = code.trim().lowercase()
        if (cleanCode.length < 4) return@withContext null

        // 1. Try resolving via Firestore
        try {
            val doc = firestore?.collection("family_invites")?.document(cleanCode)?.get()?.await()
            if (doc != null && doc.exists()) {
                val familyId = doc.getString("familyId") ?: ""
                val familyName = doc.getString("familyName") ?: "Family Circle"
                val inviterName = doc.getString("inviterName") ?: "A family member"
                val memberCount = doc.getLong("memberCount")?.toInt() ?: 1
                return@withContext PendingInvite(
                    inviteCode = cleanCode,
                    familyId = familyId,
                    familyName = familyName,
                    inviterName = inviterName,
                    memberCount = memberCount
                )
            }
        } catch (e: Exception) {
            // Offline or rules restriction
        }

        // 2. Check local fallback (e.g. if created on this device or previous session)
        _currentFamily.value?.let { current ->
            if (current.inviteCode.equals(cleanCode, ignoreCase = true)) {
                return@withContext PendingInvite(
                    inviteCode = current.inviteCode,
                    familyId = current.id,
                    familyName = current.name,
                    inviterName = current.creatorName,
                    memberCount = current.members.size
                )
            }
        }

        // 3. Fallback generic invite preview if network is unavailable
        PendingInvite(
            inviteCode = cleanCode,
            familyId = "pending_$cleanCode",
            familyName = "Holy Family Circle",
            inviterName = "Your Family",
            memberCount = 1
        )
    }

    /**
     * Sets a pending invite to trigger the Join Family prompt dialog.
     */
    fun setPendingInvite(invite: PendingInvite?) {
        _pendingInvite.value = invite
    }

    /**
     * Joins the family identified by the invite code.
     */
    suspend fun joinFamilyByCode(inviteCode: String, memberName: String): Result<FamilyGroup> = withContext(Dispatchers.IO) {
        val cleanCode = inviteCode.trim().lowercase()
        val uid = getUserId()
        val cleanName = memberName.ifBlank { getUserName() }
        setUserName(cleanName)

        try {
            // 1. Look up in Firestore
            var familyGroup: FamilyGroup? = null
            val inviteDoc = firestore?.collection("family_invites")?.document(cleanCode)?.get()?.await()
            val familyId = inviteDoc?.getString("familyId")

            if (!familyId.isNullOrBlank()) {
                val famDoc = firestore?.collection("families")?.document(familyId)?.get()?.await()
                if (famDoc != null && famDoc.exists()) {
                    familyGroup = famDoc.toObject(FamilyGroup::class.java)
                }
            }

            if (familyGroup == null) {
                // If offline or Firestore lookup failed, create or join the local representation
                familyGroup = _currentFamily.value?.takeIf { it.inviteCode.equals(cleanCode, ignoreCase = true) }
                    ?: FamilyGroup(
                        id = familyId ?: "fam_$cleanCode",
                        name = inviteDoc?.getString("familyName") ?: "Family Circle",
                        inviteCode = cleanCode,
                        creatorId = "creator",
                        creatorName = inviteDoc?.getString("inviterName") ?: "Family Member",
                        members = emptyList(),
                        totalRosaries = 0
                    )
            }

            // Check if already a member
            val existingMember = familyGroup.members.find { it.id == uid }
            val updatedMembers = if (existingMember != null) {
                familyGroup.members.map { if (it.id == uid) it.copy(name = cleanName) else it }
            } else {
                familyGroup.members + FamilyMember(
                    id = uid,
                    name = cleanName,
                    role = "Family Member",
                    joinedAt = System.currentTimeMillis(),
                    rosariesPrayed = 0
                )
            }

            val joinedGroup = familyGroup.copy(members = updatedMembers)
            saveFamilyLocallyAndCloud(joinedGroup)
            _pendingInvite.value = null

            Result.success(joinedGroup)
        } catch (e: Exception) {
            // Local fallback
            val newMember = FamilyMember(
                id = uid,
                name = cleanName,
                role = "Family Member"
            )
            val fallbackGroup = FamilyGroup(
                id = "fam_$cleanCode",
                name = "Family Circle",
                inviteCode = cleanCode,
                creatorId = "creator",
                creatorName = "Family Member",
                members = listOf(newMember)
            )
            saveFamilyLocallyAndCloud(fallbackGroup)
            _pendingInvite.value = null
            Result.success(fallbackGroup)
        }
    }

    /**
     * Leave the current family circle.
     */
    suspend fun leaveFamily() = withContext(Dispatchers.IO) {
        val current = _currentFamily.value ?: return@withContext
        val uid = getUserId()

        // Remove from cloud if possible
        try {
            val updatedMembers = current.members.filter { it.id != uid }
            if (updatedMembers.isEmpty()) {
                firestore?.collection("families")?.document(current.id)?.delete()?.await()
                firestore?.collection("family_invites")?.document(current.inviteCode)?.delete()?.await()
            } else {
                firestore?.collection("families")?.document(current.id)
                    ?.update("members", updatedMembers)
            }
        } catch (e: Exception) {
            // Non-blocking
        }

        _currentFamily.value = null
        context.familyDataStore.edit { it.remove(Keys.ACTIVE_FAMILY_JSON) }
    }

    /**
     * Updates the family prayer intention.
     */
    suspend fun updateFamilyIntention(newIntention: String) = withContext(Dispatchers.IO) {
        val current = _currentFamily.value ?: return@withContext
        val updated = current.copy(familyIntention = newIntention.trim())
        saveFamilyLocallyAndCloud(updated)
    }

    /**
     * Increments the rosary prayer count for this user and the family.
     */
    suspend fun recordRosaryForFamily() = withContext(Dispatchers.IO) {
        val current = _currentFamily.value ?: return@withContext
        val uid = getUserId()

        val updatedMembers = current.members.map { member ->
            if (member.id == uid) {
                member.copy(rosariesPrayed = member.rosariesPrayed + 1)
            } else member
        }

        val updated = current.copy(
            members = updatedMembers,
            totalRosaries = current.totalRosaries + 1
        )
        saveFamilyLocallyAndCloud(updated)
    }

    private suspend fun saveFamilyLocallyAndCloud(family: FamilyGroup) {
        _currentFamily.value = family

        // Save locally
        try {
            val jsonString = json.encodeToString(family)
            context.familyDataStore.edit { it[Keys.ACTIVE_FAMILY_JSON] = jsonString }
        } catch (e: Exception) {
            // Local serialization error handling
        }

        // Save to Firestore
        try {
            firestore?.collection("families")?.document(family.id)
                ?.set(family, SetOptions.merge())
        } catch (e: Exception) {
            // Network / Firestore error handling
        }
    }

    private suspend fun registerInviteCodeInFirestore(inviteCode: String, family: FamilyGroup) {
        try {
            val inviteData = hashMapOf(
                "inviteCode" to inviteCode,
                "familyId" to family.id,
                "familyName" to family.name,
                "inviterName" to family.creatorName,
                "memberCount" to family.members.size,
                "createdAt" to System.currentTimeMillis()
            )
            firestore?.collection("family_invites")?.document(inviteCode)
                ?.set(inviteData, SetOptions.merge())
        } catch (e: Exception) {
            // Non-blocking
        }
    }

    private suspend fun syncWithFirestore(familyId: String) {
        try {
            val doc = firestore?.collection("families")?.document(familyId)?.get()?.await()
            if (doc != null && doc.exists()) {
                val cloudFamily = doc.toObject(FamilyGroup::class.java)
                if (cloudFamily != null) {
                    _currentFamily.value = cloudFamily
                    val jsonString = json.encodeToString(cloudFamily)
                    context.familyDataStore.edit { it[Keys.ACTIVE_FAMILY_JSON] = jsonString }
                }
            }
        } catch (e: Exception) {
            // Offline fallback
        }
    }
}
