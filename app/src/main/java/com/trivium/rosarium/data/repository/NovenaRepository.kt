package com.trivium.rosarium.data.repository

import com.trivium.rosarium.data.model.Novena
import com.trivium.rosarium.data.model.NovenaDay

class NovenaRepository {

    fun getAllNovenas(): List<Novena> = novenasList

    fun getNovenaById(id: String): Novena? = novenasList.find { it.id == id }

    companion object {
        private val novenasList = listOf(
            createHolySpiritNovena(),
            createStAnthonyNovena(),
            createStJudeNovena(),
            createDivineMercyNovena(),
            createUndoerOfKnotsNovena(),
            createSacredHeartNovena(),
            createStJosephNovena(),
            createMiraculousMedalNovena(),
            createStThereseNovena()
        )

        private fun createStJudeNovena(): Novena {
            return Novena(
                id = "st_jude",
                title = "Novena to Saint Jude Thaddeus",
                subtitle = "Patron of Desperate Cases & Hopeless Causes",
                patron = "Saint Jude Thaddeus, Apostle & Martyr",
                feastDay = "October 28",
                description = "Saint Jude is invoked throughout the world in cases of extreme need, grave sickness, and situations where human help seems impossible.",
                colorHex = "#15803D",
                days = listOf(
                    NovenaDay(
                        dayNumber = 1,
                        theme = "Faith in God's Infinite Mercy",
                        scriptureReference = "Matthew 17:20",
                        scriptureText = "Truly I tell you, if you have faith as small as a mustard seed, nothing will be impossible for you.",
                        intention = "For those who feel abandoned and on the brink of losing all hope in their trials.",
                        meditation = "St. Jude walked with Christ and witnessed His divine power over death, sickness, and sorrow. When all earthly avenues are exhausted, the intercession of St. Jude reminds us that God's grace begins where human power ends.",
                        prayer = listOf(
                            "O Most Holy Apostle, Saint Jude, faithful servant and friend of Jesus,",
                            "the Church honors and invokes you universally as the patron of hopeless cases.",
                            "Pray for me, who am so miserable. Make use, I implore you, of that particular privilege accorded to you,",
                            "to bring visible and speedy help where help is almost despaired of.",
                            "Come to my assistance in this great need, that I may receive the consolation and help of Heaven in all my tribulations,",
                            "especially (mention your petition), and that I may praise God with you throughout eternity. Amen."
                        )
                    ),
                    NovenaDay(
                        dayNumber = 2,
                        theme = "Deliverance from Anguish",
                        scriptureReference = "Psalm 34:17",
                        scriptureText = "When the righteous cry for help, the Lord hears and delivers them out of all their troubles.",
                        intention = "For all suffering from anxiety, mental anguish, and dark valleys of spiritual trial.",
                        meditation = "St. Jude endured hardship and martyrdom out of supreme love for Christ. He understands our anguish and presents our sighs directly before the throne of the Lamb.",
                        prayer = listOf(
                            "O Holy Saint Jude, apostle and martyr, illustrious by reason of your glorious virtues,",
                            "look down with compassion upon me in my deep anxiety.",
                            "Obtain for me peace of mind, serenity of spirit, and the grace to surrender my will to the Holy Will of the Father.",
                            "Saint Jude, helper of the hopeless, pray for us and hear our plea. Amen."
                        )
                    ),
                    NovenaDay(
                        dayNumber = 3,
                        theme = "Perseverance in Suffering",
                        scriptureReference = "Romans 5:3-4",
                        scriptureText = "Suffering produces endurance, and endurance produces character, and character produces hope.",
                        intention = "For patience and fortitude in prolonged spiritual, physical, or financial distress.",
                        meditation = "Suffering is transformed when united with the Cross of our Lord Jesus Christ. St. Jude teaches us to never grow bitter, but to trust that trials refine our souls like gold in the furnace.",
                        prayer = listOf(
                            "Saint Jude, courageous apostle who remained faithful unto death,",
                            "teach me to carry my cross with dignity, humility, and steadfast faith.",
                            "Grant me the perseverance never to despair, knowing that God works all things for good to those who love Him. Amen."
                        )
                    ),
                    NovenaDay(
                        dayNumber = 4,
                        theme = "Restoration of Health and Strength",
                        scriptureReference = "Jeremiah 30:17",
                        scriptureText = "For I will restore health to you, and your wounds I will heal, declares the Lord.",
                        intention = "For the sick, the bedridden, and all suffering from incurable and terminal diseases.",
                        meditation = "Christ sent the Apostles out to lay hands on the sick and heal them. St. Jude continues this healing ministry in the Communion of Saints through his powerful intercession.",
                        prayer = listOf(
                            "O Blessed Saint Jude, you who were instrumental in God's miraculous healings,",
                            "look with mercy upon all who are suffering in body and mind.",
                            "Ask the Divine Physician to touch their wounds and restore their vitality if it be His Holy Will. Amen."
                        )
                    ),
                    NovenaDay(
                        dayNumber = 5,
                        theme = "Reconciliation in Families",
                        scriptureReference = "Colossians 3:13",
                        scriptureText = "Bear with each other and forgive one another if any of you has a grievance against someone. Forgive as the Lord forgave you.",
                        intention = "For fractured families, broken marriages, and estranged loved ones.",
                        meditation = "Christ came to bring reconciliation and unity. St. Jude, cousin of the Lord, intercedes to soften hardened hearts and mend broken familial bonds.",
                        prayer = listOf(
                            "Saint Jude, peacemaker and loving kinsman of Christ,",
                            "bring harmony and mutual forgiveness to our families.",
                            "Heal long-standing grievances, cast away bitterness, and rekindle charity among us. Amen."
                        )
                    ),
                    NovenaDay(
                        dayNumber = 6,
                        theme = "Spiritual Renewal and Freedom",
                        scriptureReference = "2 Corinthians 5:17",
                        scriptureText = "If anyone is in Christ, the new creation has come: The old has gone, the new is here!",
                        intention = "For freedom from persistent habits of sin, addiction, and spiritual lethargy.",
                        meditation = "The power of God's grace breaks all chains. St. Jude intercedes for souls trapped in bondage, that they may experience the true liberation of Christ.",
                        prayer = listOf(
                            "Glorious Apostle Saint Jude, champion of the truth,",
                            "break the bonds of vice and despair that hold my soul captive.",
                            "Lead me back to the Sacrament of Penance and the living fountain of Grace. Amen."
                        )
                    ),
                    NovenaDay(
                        dayNumber = 7,
                        theme = "Trust Against All Odds",
                        scriptureReference = "Hebrews 11:1",
                        scriptureText = "Now faith is confidence in what we hope for and assurance about what we do not see.",
                        intention = "For trust when every human door is shut and the path is completely dark.",
                        meditation = "Faith does not mean seeing the path clearly, but trusting the Hand that guides us in the night. St. Jude's flame of the Holy Spirit lights our darkest hours.",
                        prayer = listOf(
                            "O Saint Jude, bearer of the light of Christ to distant lands,",
                            "illuminate my darkness. Help me to trust without seeing and to hope against all hope,",
                            "resting securely in the Sacred Heart of Jesus. Amen."
                        )
                    ),
                    NovenaDay(
                        dayNumber = 8,
                        theme = "Gratitude and Giving Witness",
                        scriptureReference = "Psalm 107:1",
                        scriptureText = "Give thanks to the Lord, for he is good; his love endures forever.",
                        intention = "For a heart that gives thanks in advance for God's blessings and promises.",
                        meditation = "Even before our prayers are visibly answered, faith thanks God for His goodness. St. Jude encourages us to proclaim God's mercy to all we meet.",
                        prayer = listOf(
                            "Beloved Saint Jude, I promise to be ever mindful of this great favor,",
                            "to always honor you as my special and powerful patron, and gratefully to encourage devotion to you.",
                            "Glory be to the Father, and to the Son, and to the Holy Spirit. Amen."
                        )
                    ),
                    NovenaDay(
                        dayNumber = 9,
                        theme = "Surrender to the Father's Will",
                        scriptureReference = "Luke 22:42",
                        scriptureText = "Father, if you are willing, take this cup from me; yet not my will, but yours be done.",
                        intention = "For total surrender and peace as we conclude this novena.",
                        meditation = "The summit of Christian prayer is holy abandonment to God's providence. With St. Jude, we place all our desires at the foot of the Cross, confident in God's eternal love.",
                        prayer = listOf(
                            "O Holy Saint Jude, through your ardent love for Jesus and Mary,",
                            "obtain for me the supreme grace of loving God above all things and accepting His Holy Will with filial joy.",
                            "May the Most Sacred Heart of Jesus be adored and loved in all the tabernacles until the end of time. Amen."
                        )
                    )
                )
            )
        }

        private fun createDivineMercyNovena(): Novena {
            return Novena(
                id = "divine_mercy",
                title = "Divine Mercy Novena",
                subtitle = "Dictated by Our Lord to Saint Maria Faustina Kowalska",
                patron = "Saint Faustina Kowalska & The Merciful Jesus",
                feastDay = "Divine Mercy Sunday (Second Sunday of Easter)",
                description = "Our Lord instructed St. Faustina: 'By this novena, I will grant every possible grace to souls.' Each day encompasses a specific group of souls submerged in the Ocean of His Mercy.",
                colorHex = "#DC2626",
                days = listOf(
                    NovenaDay(
                        dayNumber = 1,
                        theme = "All Mankind and Sinners",
                        scriptureReference = "1 Timothy 2:3-4",
                        scriptureText = "God our Savior desires everyone to be saved and to come to the knowledge of the truth.",
                        intention = "Today bring to Me all mankind, especially all sinners, and immerse them in the ocean of My mercy.",
                        meditation = "Jesus desires that not a single soul perish. His mercy is greater than the deepest abyss of human guilt. We bring every sinner to the Fountain of Life.",
                        prayer = listOf(
                            "Most Merciful Jesus, whose very nature is to have compassion on us and to forgive us,",
                            "do not look upon our sins but upon our trust which we place in Your infinite goodness.",
                            "Receive us all into the abode of Your Most Compassionate Heart, and never let us escape from It.",
                            "Eternal Father, turn Your merciful gaze upon all mankind and especially upon poor sinners,",
                            "whose only hope is the Most Compassionate Heart of Your Son, our Lord Jesus Christ. Amen."
                        )
                    ),
                    NovenaDay(
                        dayNumber = 2,
                        theme = "The Souls of Priests and Religious",
                        scriptureReference = "Hebrews 7:24-25",
                        scriptureText = "He holds his priesthood permanently, because he continues forever. Consequently, he is able to save to the uttermost those who draw near to God through him.",
                        intention = "Today bring to Me the souls of priests and religious, and immerse them in My unfathomable mercy.",
                        meditation = "Through priests and consecrated souls, the torrents of Divine Mercy flow to the world. We pray for their holiness, fidelity, and strength against every temptation.",
                        prayer = listOf(
                            "Most Merciful Jesus, from whom comes all that is good,",
                            "increase Your grace in men and women consecrated to Your service, that they may perform worthy works of mercy;",
                            "and that all who see them may glorify the Father of Mercy who is in heaven.",
                            "Eternal Father, cast Your merciful gaze upon the company of chosen ones in Your vineyard. Amen."
                        )
                    ),
                    NovenaDay(
                        dayNumber = 3,
                        theme = "Devout and Faithful Souls",
                        scriptureReference = "Revelation 2:10",
                        scriptureText = "Be faithful unto death, and I will give you the crown of life.",
                        intention = "Today bring to Me all devout and faithful souls, and immerse them in the ocean of My mercy.",
                        meditation = "These souls brought consolation to Jesus on the Way of the Cross; they were a drop of relief in an ocean of bitterness.",
                        prayer = listOf(
                            "Most Merciful Jesus, from the treasury of Your mercy, You impart Your graces in great abundance to each and all.",
                            "Receive us into the abode of Your Most Compassionate Heart and never let us depart from It.",
                            "Eternal Father, turn Your merciful gaze upon faithful souls, as upon the inheritance of Your Son. Amen."
                        )
                    ),
                    NovenaDay(
                        dayNumber = 4,
                        theme = "Those Who Do Not Believe in God",
                        scriptureReference = "John 8:12",
                        scriptureText = "I am the light of the world. Whoever follows me will never walk in darkness, but will have the light of life.",
                        intention = "Today bring to Me those who do not believe in God and those who do not yet know Me.",
                        meditation = "Jesus suffered His bitter Passion also for souls that have not yet recognized Him. May the brightness of His truth illuminate them.",
                        prayer = listOf(
                            "Most compassionate Jesus, You are the Light of the whole world.",
                            "Receive into the abode of Your Most Compassionate Heart the souls of those who do not yet believe in God or know You.",
                            "Let the rays of Your grace enlighten them that they too, together with us, may praise Your wonderful mercy. Amen."
                        )
                    ),
                    NovenaDay(
                        dayNumber = 5,
                        theme = "Those Who Have Separated from the Church",
                        scriptureReference = "John 17:21",
                        scriptureText = "That they may all be one, just as you, Father, are in me, and I in you.",
                        intention = "Today bring to Me the souls of those who have separated themselves from My Church.",
                        meditation = "Division in the Body of Christ rends the Heart of Jesus. We pray for unity and for all who wander far from the sheepfold of Mother Church.",
                        prayer = listOf(
                            "Most Merciful Jesus, Goodness Itself, You do not refuse light to those who seek it of You.",
                            "Receive into the abode of Your Most Compassionate Heart the souls of those who have separated themselves from Your Church.",
                            "Draw them by Your light into the unity of the Church, and preserve them in Your love. Amen."
                        )
                    ),
                    NovenaDay(
                        dayNumber = 6,
                        theme = "The Meek, Humble Souls and Little Children",
                        scriptureReference = "Matthew 11:29",
                        scriptureText = "Take my yoke upon you, and learn from me, for I am gentle and lowly in heart, and you will find rest for your souls.",
                        intention = "Today bring to Me the meek and humble souls and the souls of little children.",
                        meditation = "These souls most closely resemble Christ. They send fragrance up to the throne of God; God Himself delights in them.",
                        prayer = listOf(
                            "Most Merciful Jesus, You Yourself have said, 'Learn from Me for I am meek and humble of heart.'",
                            "Receive into Your Heart all meek and humble souls and little children.",
                            "A truly humble and meek soul already anticipates heaven on earth. Amen."
                        )
                    ),
                    NovenaDay(
                        dayNumber = 7,
                        theme = "Souls Who Extol and Venterate Mercy",
                        scriptureReference = "Luke 6:36",
                        scriptureText = "Be merciful, even as your Father is merciful.",
                        intention = "Today bring to Me the souls who especially venerate and glorify My mercy.",
                        meditation = "These souls sorrow most over the Passion of Jesus and enter most deeply into His spirit. They are living images of His compassionate Heart.",
                        prayer = listOf(
                            "Most Merciful Jesus, whose Heart is Love Itself, receive into Your Heart the souls who extol Your great mercy.",
                            "Shield them in life and especially at the hour of their death with the unfathomable depth of Your protection. Amen."
                        )
                    ),
                    NovenaDay(
                        dayNumber = 8,
                        theme = "The Holy Souls in Purgatory",
                        scriptureReference = "2 Maccabees 12:46",
                        scriptureText = "It is therefore a holy and wholesome thought to pray for the dead, that they may be loosed from sins.",
                        intention = "Today bring to Me the souls who are detained in purgatory, and submerge them in the depths of My mercy.",
                        meditation = "The suffering souls are purifying their garments for the celestial nuptials. Our prayers and the Blood of Jesus relieve their scorching flames.",
                        prayer = listOf(
                            "Most Merciful Jesus, You Yourself have said that You desire mercy; so I bring to the abode of Your Heart the souls in Purgatory.",
                            "May the streams of Blood and Water gushing from Your Heart quench the fiery cleansing flames.",
                            "Eternal Father, turn Your merciful gaze upon these suffering souls and grant them eternal rest. Amen."
                        )
                    ),
                    NovenaDay(
                        dayNumber = 9,
                        theme = "Lukewarm Souls",
                        scriptureReference = "Revelation 3:15-16",
                        scriptureText = "I know your deeds, that you are neither cold nor hot. So because you are lukewarm, I will spit you out of my mouth.",
                        intention = "Today bring to Me souls who have become lukewarm, and immerse them in the abyss of My mercy.",
                        meditation = "Lukewarm souls caused Jesus the most painful torments in the Garden of Olives. Only His limitless mercy can revive the dying embers of their faith.",
                        prayer = listOf(
                            "Most Compassionate Jesus, You are Compassion Itself. I bring lukewarm souls into Your Heart.",
                            "Let these frozen souls, who are like corpses, be enkindled again with the flame of Your pure love.",
                            "O compassionate Jesus, exercise the omnipotence of Your mercy and draw them into the very ardor of Your love. Amen."
                        )
                    )
                )
            )
        }

        private fun createUndoerOfKnotsNovena(): Novena {
            return Novena(
                id = "undoer_of_knots",
                title = "Novena to Our Lady, Undoer of Knots",
                subtitle = "Virgen Desatanudos • Championed by Pope Francis",
                patron = "The Blessed Virgin Mary, Undoer of Knots",
                feastDay = "September 28",
                description = "Based on St. Irenaeus's 2nd-century insight: 'The knot of Eve's disobedience was loosed by Mary's obedience.' Mary unties the impossible tangled knots of our spiritual and earthly lives.",
                colorHex = "#1D4ED8",
                days = listOf(
                    NovenaDay(
                        dayNumber = 1,
                        theme = "Untying the Knot of Fear and Anxiety",
                        scriptureReference = "Philippians 4:6-7",
                        scriptureText = "Do not be anxious about anything, but in every situation, by prayer and petition, present your requests to God.",
                        intention = "For release from paralyzing dread, anxiety over the future, and lack of trust in Providence.",
                        meditation = "Dearest Mother, in your immaculate hands no knot remains tangled. Take today the knot of fear that suffocates my heart and loosen it through the grace of your Son.",
                        prayer = listOf(
                            "Holy Mary, full of God’s presence during the days of your life,",
                            "you accepted with full humility the Father’s will, and the devil was never capable of tying you up in his confusion.",
                            "As you interceded for our needs with your Son, you give us an example of how to untie the knots of our lives.",
                            "By ever remaining as our Mother, you arrange and make clear the ties that unite us unto the Lord.",
                            "Holy Mother, Mother of God and our Mother, unto you who untie with a motherly heart the knots of our lives,",
                            "I ask you to receive into your hands (mention your knot/petition), and to free it from the snares and confusion that surround it.",
                            "Through your grace, your intercession, and your example, deliver us from all evil, Our Lady,",
                            "and untie the knots that prevent us from being united with God. Amen."
                        )
                    ),
                    NovenaDay(
                        dayNumber = 2,
                        theme = "Untying the Knot of Resentment and Bitterness",
                        scriptureReference = "Ephesians 4:31-32",
                        scriptureText = "Get rid of all bitterness, rage and anger... Be kind and compassionate to one another, forgiving each other.",
                        intention = "For the grace to forgive those who have deeply wounded us and to release old grudges.",
                        meditation = "Resentment binds our soul with tight cords. Mother Mary stood at Calvary and offered forgiveness with her Dying Son. In her maternal hands, bitterness melts into mercy.",
                        prayer = listOf(
                            "Mary, beloved Mother, channel of all grace, I return to you today.",
                            "Into your gentle hands I surrender the knot of resentment, unforgiveness, and old hurt.",
                            "Ask Jesus to grant me a clean, forgiving heart that reflects the boundless pardon of God. Amen."
                        )
                    ),
                    NovenaDay(
                        dayNumber = 3,
                        theme = "Untying the Knot of Broken Relationships",
                        scriptureReference = "1 Corinthians 13:4-7",
                        scriptureText = "Love is patient, love is kind. It does not envy, it does not boast, it is not proud... It always protects, always trusts, always hopes, always perseveres.",
                        intention = "For marriages in distress, broken friendships, and estranged family members.",
                        meditation = "At Cana, Mary noticed the wine was running short. She notices when love runs thin in our human homes and intercedes with Jesus to perform a miracle of restoration.",
                        prayer = listOf(
                            "Mother of Fair Love, look upon my broken relationships.",
                            "Untie the knots of misunderstanding, stubborn pride, and silence.",
                            "Pour the new wine of Christ's charity into our hearts and homes. Amen."
                        )
                    ),
                    NovenaDay(
                        dayNumber = 4,
                        theme = "Untying the Knot of Habitual Sin",
                        scriptureReference = "Romans 6:14",
                        scriptureText = "For sin shall no longer be your master, because you are not under the law, but under grace.",
                        intention = "For complete freedom from vices, addictions, and recurring falls into sin.",
                        meditation = "Satan wraps his cords around our weaknesses until we feel powerless. Mary, the Woman who crushes the serpent's head, severs these chains through her spotless purity.",
                        prayer = listOf(
                            "O Queen of Heaven, into your pure hands I place this habitual sin that drags me down.",
                            "Free me from the snares of the adversary and grant me the grace of true conversion and holy perseverance. Amen."
                        )
                    ),
                    NovenaDay(
                        dayNumber = 5,
                        theme = "Untying the Knot of Financial and Material Ruin",
                        scriptureReference = "Matthew 6:33",
                        scriptureText = "Seek first his kingdom and his righteousness, and all these things will be given to you as well.",
                        intention = "For those drowning in debt, unemployment, or destitute living conditions.",
                        meditation = "Joseph and Mary lived in humble poverty in Nazareth, yet God provided every necessity. Mary knows our material trials and asks the Father to open doors where none exist.",
                        prayer = listOf(
                            "Mother of Mercy, you know the temporal burdens that weigh upon my shoulders.",
                            "Take the knot of financial insecurity and distress, and provide for our honest needs according to the riches of God's glory. Amen."
                        )
                    ),
                    NovenaDay(
                        dayNumber = 6,
                        theme = "Untying the Knot of Physical and Mental Affliction",
                        scriptureReference = "Psalm 147:3",
                        scriptureText = "He heals the brokenhearted and binds up their wounds.",
                        intention = "For those enduring bodily illness, chronic pain, or mental depression.",
                        meditation = "Mary held the broken body of Jesus after the Crucifixion. She tenderly embraces every sick child of hers and obtains spiritual strength and bodily relief.",
                        prayer = listOf(
                            "Health of the Sick and Comforter of the Afflicted,",
                            "untie the knots of infirmity, depression, and weariness.",
                            "May your comforting mantle cover all who weep and suffer this day. Amen."
                        )
                    ),
                    NovenaDay(
                        dayNumber = 7,
                        theme = "Untying the Knot of Spiritual Coldness",
                        scriptureReference = "Romans 12:11",
                        scriptureText = "Never be lacking in zeal, but keep your spiritual fervor, serving the Lord.",
                        intention = "For a renewed hunger for the Holy Eucharist, personal prayer, and holy Scripture.",
                        meditation = "When prayer becomes dry and God feels distant, Mary remains beside us like she was with the Apostles in the Upper Room, calling down the Fire of the Holy Spirit.",
                        prayer = listOf(
                            "Mother Most Pure, re-ignite the fire of divine charity in my lukewarm soul.",
                            "Untie the knot of sloth and neglect, and draw me close to the Holy Tabernacle of your Son. Amen."
                        )
                    ),
                    NovenaDay(
                        dayNumber = 8,
                        theme = "Untying the Knot of Despair",
                        scriptureReference = "Romans 15:13",
                        scriptureText = "May the God of hope fill you with all joy and peace as you trust in him, so that you may overflow with hope by the power of the Holy Spirit.",
                        intention = "For all who have lost their will to live and feel life is without purpose.",
                        meditation = "Mary stood under the darkness of Golgotha without despairing. She is the Star of the Sea guiding shipwrecked souls safely to the harbor of eternal hope.",
                        prayer = listOf(
                            "Refuge of Sinners and Gate of Heaven,",
                            "when all light seems extinguished, be my guiding star.",
                            "Disperse the dark clouds of despair and fill my heart with radiant Christian hope. Amen."
                        )
                    ),
                    NovenaDay(
                        dayNumber = 9,
                        theme = "Thanksgiving and Total Dedication",
                        scriptureReference = "Luke 1:46-47",
                        scriptureText = "My soul glorifies the Lord and my spirit rejoices in God my Savior.",
                        intention = "In thanksgiving for Mary's maternal care and dedicated surrender to God's reign.",
                        meditation = "As this novena concludes, we praise God for giving us Mary as our Mother. With her, our life becomes a perpetual Magnificat of thanksgiving.",
                        prayer = listOf(
                            "O Mary, Undoer of Knots, generous Mother, I thank you with all my heart.",
                            "You have unloosed the knots of my misery. I consecrate my life, my family, and all my desires to you forever.",
                            "Keep me always under your mantle of protection and lead me to the eternal glory of Christ. Amen."
                        )
                    )
                )
            )
        }

        private fun createSacredHeartNovena(): Novena {
            return Novena(
                id = "sacred_heart",
                title = "Efficacious Novena to the Sacred Heart",
                subtitle = "Prayed Daily by Saint Padre Pio for All Intentions",
                patron = "The Most Sacred Heart of Jesus",
                feastDay = "Solemnity of the Most Sacred Heart (Friday after Corpus Christi)",
                description = "St. Margaret Mary Alacoque received the promises of the Sacred Heart, and Padre Pio prayed this exact three-fold novena daily for thousands of petitions.",
                colorHex = "#991B1B",
                days = (1..9).map { day ->
                    NovenaDay(
                        dayNumber = day,
                        theme = "Day $day: Trust in Christ's Eternal Promises",
                        scriptureReference = "Matthew 7:7-8",
                        scriptureText = "Ask, and it will be given you; seek, and you will find; knock, and it will be opened to you.",
                        intention = "For complete confidence in the infallible promises of the Most Sacred Heart of Jesus.",
                        meditation = "Padre Pio always reminded souls: 'Pray, hope, and don't worry.' Christ Himself promised: 'I will grant them all the graces necessary for their state of life. I will give peace in their families.'",
                        prayer = listOf(
                            "O my Jesus, You have said: 'Truly I say to you, ask and you will receive, seek and you will find, knock and it will be opened to you.'",
                            "Behold I knock, I seek and I ask for the grace of (mention your intention).",
                            "Our Father... Hail Mary... Glory Be...",
                            "Sacred Heart of Jesus, I place all my trust in You.",
                            "O my Jesus, You have said: 'Truly I say to you, if you ask anything of the Father in My name, He will give it to you.'",
                            "Behold, in Your name, I ask the Father for the grace of (mention your intention).",
                            "Our Father... Hail Mary... Glory Be...",
                            "Sacred Heart of Jesus, I place all my trust in You.",
                            "O my Jesus, You have said: 'Truly I say to you, heaven and earth will pass away but My words will not pass away.'",
                            "Encouraged by Your infallible words I now ask for the grace of (mention your intention).",
                            "Our Father... Hail Mary... Glory Be...",
                            "Sacred Heart of Jesus, I place all my trust in You. Amen."
                        )
                    )
                }
            )
        }

        private fun createHolySpiritNovena(): Novena {
            return Novena(
                id = "holy_spirit",
                title = "Novena to the Holy Spirit",
                subtitle = "The Historic Nine-Day Pentecost Novena",
                patron = "The Holy Spirit, Third Person of the Trinity",
                feastDay = "Solemnity of Pentecost",
                description = "Instituted by Jesus Himself when He commanded the Apostles to wait in Jerusalem for nine days in prayer until clothed with power from on high.",
                colorHex = "#EA580C",
                days = listOf(
                    NovenaDay(1, "The Holy Spirit, Lord and Giver of Life", "John 14:16-17", "He will give you another advocate to help you and be with you forever—the Spirit of truth.", "For an outpouring of the Holy Spirit across the whole Church.", "The Holy Spirit is the soul of the Mystical Body, animating every good thought and deed.", listOf("Come, Holy Spirit, fill the hearts of Your faithful and kindle in them the fire of Your love. Send forth Your Spirit and they shall be created, and You shall renew the face of the earth. Amen.")),
                    NovenaDay(2, "The Gift of Fear of the Lord", "Proverbs 9:10", "The fear of the Lord is the beginning of wisdom.", "For holy awe and reverence before the majesty of God.", "Holy Fear frees us from worldly fear and inspires deep filial devotion.", listOf("Spirit of Holy Fear, penetrate my inmost heart that I may set the Lord God before my eyes forever. Amen.")),
                    NovenaDay(3, "The Gift of Piety", "Romans 8:15", "The Spirit you received brought about your adoption to sonship. And by him we cry, 'Abba, Father.'", "For tender love toward God as our Father and all people as our brothers.", "Piety turns duty into delightful love and fills the soul with peace.", listOf("Spirit of Piety, possess my heart and incline it to a true and filial love for God. Amen.")),
                    NovenaDay(4, "The Gift of Fortitude", "Ephesians 6:10", "Be strong in the Lord and in his mighty power.", "For courage to defend the faith and bear the cross without flinching.", "Fortitude overcomes all timidity and strengthens the martyrs in their confession.", listOf("Spirit of Fortitude, uphold my soul in time of trouble and adversity. Amen.")),
                    NovenaDay(5, "The Gift of Knowledge", "Philippians 3:8", "I consider everything a loss because of the surpassing worth of knowing Christ Jesus my Lord.", "For discernment to see created things in the light of eternity.", "Knowledge teaches us the true value of earthly things and keeps us from idols.", listOf("Spirit of Knowledge, grant that I may perceive the will of God in all things. Amen.")),
                    NovenaDay(6, "The Gift of Understanding", "Psalm 119:130", "The unfolding of your words gives light; it gives understanding to the simple.", "For illumination into the deep mysteries of our Holy Faith.", "Understanding opens the eyes of the heart to the beauty of revealed truth.", listOf("Spirit of Understanding, enlighten our minds that we may know and believe all the mysteries of salvation. Amen.")),
                    NovenaDay(7, "The Gift of Counsel", "Psalm 32:8", "I will instruct you and teach you in the way you should go; I will counsel you with my loving eye on you.", "For wise decision-making guided by the Holy Spirit.", "Counsel whispers the right path when crossroads appear in life.", listOf("Spirit of Counsel, guide me in all my ways that I may always do that which is pleasing to You. Amen.")),
                    NovenaDay(8, "The Gift of Wisdom", "James 3:17", "The wisdom that comes from heaven is first of all pure; then peace-loving, considerate, submissive, full of mercy and good fruit.", "For the highest gift: to relish and love divine things.", "Wisdom detaches us from fleeting vanity and unites the soul to God.", listOf("Spirit of Wisdom, direct all my actions and teach me to seek the kingdom of heaven. Amen.")),
                    NovenaDay(9, "The Fruits of the Holy Spirit", "Galatians 5:22-23", "The fruit of the Spirit is love, joy, peace, forbearance, kindness, goodness, faithfulness, gentleness and self-control.", "For an abundant harvest of holiness and virtue in our daily lives.", "The gifts blossom into the twelve heavenly fruits that taste of eternity.", listOf("Holy Spirit, Divine Paraclete, dwell richly within my heart and produce in me the fruits of charity, joy, and peace. Amen."))
                )
            )
        }

        private fun createStJosephNovena(): Novena {
            return Novena(
                id = "st_joseph",
                title = "Novena to Saint Joseph",
                subtitle = "Patron of the Universal Church, Fathers & Workers",
                patron = "Saint Joseph, Terror of Demons",
                feastDay = "March 19 (Solemnity) & May 1 (St. Joseph the Worker)",
                description = "St. Teresa of Avila said: 'I know not how anyone can think of the Queen of Angels without thanking St. Joseph for the assistance he gave them. I have never known anyone who was truly devoted to him fail to make great progress in virtue.'",
                colorHex = "#B45309",
                days = listOf(
                    NovenaDay(1, "Saint Joseph, Just and Faithful Man", "Matthew 1:19", "Because Joseph her husband was faithful to the law, and yet did not want to expose her to public disgrace, he had in mind to divorce her quietly.", "For integrity, honesty, and righteousness in our daily duties.", "St. Joseph's justice was steeped in mercy and silent obedience to God.", listOf("O Saint Joseph, foster father of Jesus and chaste spouse of Mary, you were declared just by the Holy Spirit. Obtain for us a pure and upright heart. Amen.")),
                    NovenaDay(2, "Saint Joseph, Man of Holy Silence", "Proverbs 10:19", "Sin is not ended by multiplying words, but the prudent hold their tongues.", "For custody of the tongue and depth of contemplative prayer.", "No spoken word of Joseph is recorded in Scripture, yet his deeds spoke of total surrender.", listOf("Silent Guardian of the Redeemer, teach us to quiet the clamor of the world and listen to the voice of God. Amen.")),
                    NovenaDay(3, "Saint Joseph, Diligent Worker", "Colossians 3:23", "Whatever you do, work at it with all your heart, as working for the Lord, not for human masters.", "For all workers, the unemployed, and sanctification of honest labor.", "In the carpenter's workshop at Nazareth, Joseph taught the Creator of the universe how to work with wood.", listOf("Saint Joseph the Worker, bless our daily labors and help us offer them as a pleasing sacrifice to God. Amen.")),
                    NovenaDay(4, "Saint Joseph, Pillar of Families", "Joshua 24:15", "As for me and my household, we will serve the Lord.", "For protection and sanctification of all Christian homes.", "Joseph protected the Holy Family from danger, poverty, and exile.", listOf("Pillar of Families, protect our homes against discord, secularism, and spiritual breakdown. Amen.")),
                    NovenaDay(5, "Saint Joseph, Model of Purity", "Matthew 5:8", "Blessed are the pure in heart, for they will see God.", "For chastity in our thoughts, actions, and states of life.", "As the virginal husband of the Virgin Mary, Joseph's purity was radiant and vigilant.", listOf("Chaste Guardian of the Virgin, preserve in our souls the immaculate purity of Christ. Amen.")),
                    NovenaDay(6, "Saint Joseph, Obedient to the Angel", "Matthew 2:13", "An angel of the Lord appeared to Joseph in a dream. 'Get up,' he said, 'take the child and his mother and escape to Egypt.'", "For prompt obedience to God's commandments without delay or hesitation.", "Whenever God commanded, Joseph rose immediately, even in the middle of the night.", listOf("Faithful Joseph, teach us prompt, cheerful obedience to God's holy law. Amen.")),
                    NovenaDay(7, "Saint Joseph, Solace of the Afflicted", "Psalm 46:1", "God is our refuge and strength, an ever-present help in trouble.", "For all who carry secret griefs, financial worries, and hardships.", "Joseph knows the pangs of poverty and the fear of persecution, and he rushes to help his children.", listOf("Solace of the Wretched, console our aching hearts and plead for our necessities. Amen.")),
                    NovenaDay(8, "Saint Joseph, Terror of Demons", "James 4:7", "Submit yourselves, then, to God. Resist the devil, and he will flee from you.", "For spiritual warfare and protection against evil influences.", "Demons flee from the humility and authority of Saint Joseph, guardian of the Savior.", listOf("Terror of Demons, defend our souls, our children, and the Holy Catholic Church against the snares of Satan. Amen.")),
                    NovenaDay(9, "Saint Joseph, Patron of a Happy Death", "Psalm 116:15", "Precious in the sight of the Lord is the death of his faithful servants.", "For the dying and for our own final perseverance.", "Joseph died peacefully in the arms of Jesus and Mary—the happiest death imaginable.", listOf("Patron of a Happy Death, be near us in our final hour, and escort our souls into the light of paradise. Amen."))
                )
            )
        }

        private fun createMiraculousMedalNovena(): Novena {
            return Novena(
                id = "miraculous_medal",
                title = "Novena of the Miraculous Medal",
                subtitle = "Given to Saint Catherine Labouré in Paris, 1830",
                patron = "Our Lady of the Miraculous Medal",
                feastDay = "November 27",
                description = "Our Lady revealed to St. Catherine: 'Have a medal struck upon this model. Those who wear it with confidence will receive great graces, especially if they wear it around the neck.'",
                colorHex = "#0284C7",
                days = (1..9).map { day ->
                    NovenaDay(
                        dayNumber = day,
                        theme = "Day $day: O Mary Conceived Without Sin, Pray for Us",
                        scriptureReference = "Genesis 3:15",
                        scriptureText = "I will put enmity between you and the woman, and between your offspring and hers; he will crush your head.",
                        intention = "For graces of conversion, healing, and protection through the Miraculous Medal.",
                        meditation = "The rays of light streaming from Mary's hands represent the graces she bestows upon those who ask for them. With filial confidence, we ask for our necessities.",
                        prayer = listOf(
                            "O Immaculate Virgin Mary, Mother of Our Lord Jesus and our Mother,",
                            "penetrated with the most lively confidence in your all-powerful and never-failing intercession,",
                            "manifested so often through the Miraculous Medal, we your loving and trustful children implore you to obtain for us the graces and favors we ask during this novena,",
                            "if they be beneficial to our immortal souls, and the souls for whom we pray (mention your intention).",
                            "You know, O Mary, how often our souls have been the sanctuaries of your Son who hates iniquity.",
                            "Obtain for us then a deep hatred of sin and that purity of heart which will attach us to God alone.",
                            "O Mary, conceived without sin, pray for us who have recourse to thee. Amen."
                        )
                    )
                }
            )
        }

        private fun createStThereseNovena(): Novena {
            return Novena(
                id = "st_therese",
                title = "Novena of the Rose to St. Thérèse",
                subtitle = "The Little Flower • Doctor of the Church",
                patron = "Saint Thérèse of Lisieux",
                feastDay = "October 1",
                description = "St. Thérèse promised before her death: 'My mission is about to begin, my mission of making God loved as I love Him, of giving my little way to souls. I will spend my heaven doing good upon earth.'",
                colorHex = "#E11D48",
                days = (1..9).map { day ->
                    NovenaDay(
                        dayNumber = day,
                        theme = "Day $day: The Little Way of Spiritual Childhood",
                        scriptureReference = "Matthew 18:3",
                        scriptureText = "Truly I tell you, unless you change and become like little children, you will never enter the kingdom of heaven.",
                        intention = "For childlike confidence and trust in God's infinite tenderness and mercy.",
                        meditation = "The Little Way is not about doing extraordinary things, but doing ordinary things with extraordinary love. St. Thérèse promises to shower roses of heavenly grace on those who turn to her.",
                        prayer = listOf(
                            "Saint Thérèse of the Child Jesus, please pick for me a rose from the heavenly gardens and send it to me as a message of love.",
                            "O Little Flower of Jesus, ask God today to grant the favor I now place with confidence in your hands (mention your request).",
                            "Saint Thérèse, help me to always believe as you did in God's great love for me,",
                            "so that I might imitate your 'Little Way' each day. Amen."
                        )
                    )
                }
            )
        }

        private fun createStAnthonyNovena(): Novena {
            return Novena(
                id = "st_anthony",
                title = "Novena to Saint Anthony of Padua",
                subtitle = "The Wonder-Worker • Patron of Lost Things & the Poor",
                patron = "Saint Anthony of Padua, Priest and Doctor of the Church",
                feastDay = "June 13",
                description = "St. Anthony is renowned throughout the Catholic world as the 'Miracle Worker'. Millions pray this novena for the recovery of lost articles, restoring spiritual peace, family healing, and God's assistance in temporal needs.",
                colorHex = "#92400E",
                days = listOf(
                    NovenaDay(
                        dayNumber = 1,
                        theme = "Saint Anthony, Mirror of Humility",
                        scriptureReference = "Luke 14:11",
                        scriptureText = "For all those who exalt themselves will be humbled, and those who humble themselves will be exalted.",
                        intention = "For true humility of heart, purity of mind, and detachment from worldly pride.",
                        meditation = "Though blessed with immense learning and eloquence, Saint Anthony chose to wash dishes and perform the humblest monastery duties. True greatness before God is found in lowliness of spirit.",
                        prayer = listOf(
                            "O Blessed Saint Anthony, mirror of true humility, who preferred to be forgotten by the world rather than praised,",
                            "obtain for me a truly humble heart. Remove from my soul all vanity and self-righteousness.",
                            "Intercede for me before the Divine Child, and help me in my present petition (mention your request). Amen."
                        )
                    ),
                    NovenaDay(
                        dayNumber = 2,
                        theme = "Saint Anthony, Ark of the Holy Scriptures",
                        scriptureReference = "2 Timothy 3:16-17",
                        scriptureText = "All Scripture is God-breathed and is useful for teaching, rebuking, correcting and training in righteousness.",
                        intention = "For love of God's Holy Word and devotion to daily scripture meditation.",
                        meditation = "Pope Gregory IX called Saint Anthony the 'Ark of the Testament' because of his profound mastery and contemplation of the Sacred Scriptures. May God's Word be a lamp unto our feet.",
                        prayer = listOf(
                            "Saint Anthony, illustrious Doctor of the Gospel,",
                            "inspire in my soul a profound hunger for the Word of God.",
                            "Help me not only to hear the Gospel, but to live it faithfully in charity and virtue every day. Amen."
                        )
                    ),
                    NovenaDay(
                        dayNumber = 3,
                        theme = "Saint Anthony, Defender of Truth",
                        scriptureReference = "1 Peter 3:15",
                        scriptureText = "Always be prepared to give an answer to everyone who asks you to give the reason for the hope that you have.",
                        intention = "For fortitude to profess and live the Catholic Faith amidst confusion and secularism.",
                        meditation = "Saint Anthony proclaimed the truth with burning charity and gentle conviction, converting hardened hearts and guiding souls back to the Catholic Church.",
                        prayer = listOf(
                            "Courageous Apostle of Christ, Saint Anthony,",
                            "strengthen my faith when doubt or temptation surrounds me.",
                            "Give me the courage to stand steadfast in the teachings of the Church with charity and zeal. Amen."
                        )
                    ),
                    NovenaDay(
                        dayNumber = 4,
                        theme = "Saint Anthony, Father and Friend of the Poor",
                        scriptureReference = "Proverbs 19:17",
                        scriptureText = "Whoever is kind to the poor lends to the Lord, and he will reward them for what they have done.",
                        intention = "For the poor, the homeless, the destitute, and for our own generosity toward those in need.",
                        meditation = "Devotion to 'Saint Anthony's Bread' has fed millions of hungry families. Anthony gave away everything he possessed to relieve the destitute and suffering.",
                        prayer = listOf(
                            "O Generous Saint Anthony, protector of the poor and comforter of the wretched,",
                            "open my hands and heart to share what I have with those who suffer lack.",
                            "Look with tender mercy on all who are hungry, sick, or without shelter today. Amen."
                        )
                    ),
                    NovenaDay(
                        dayNumber = 5,
                        theme = "Saint Anthony, Restorer of What Was Lost",
                        scriptureReference = "Luke 19:10",
                        scriptureText = "For the Son of Man came to seek and to save the lost.",
                        intention = "For the recovery of lost articles, lost health, lost peace, and souls lost in sin.",
                        meditation = "God has granted Saint Anthony the unique charism of finding what is lost. Above all temporal possessions, he desires to help us recover lost sanctifying grace and peace of conscience.",
                        prayer = listOf(
                            "Glorious Saint Anthony, God has endowed you with the miraculous gift of restoring lost things.",
                            "I ask your powerful help in finding that which I have lost (mention lost article or spiritual gift),",
                            "and above all, preserve in me the grace of God that I may never lose my eternal salvation. Amen."
                        )
                    ),
                    NovenaDay(
                        dayNumber = 6,
                        theme = "Saint Anthony, Pure Lover of the Infant Jesus",
                        scriptureReference = "Matthew 5:8",
                        scriptureText = "Blessed are the pure in heart, for they will see God.",
                        intention = "For purity of thoughts, words, and deeds in our state of life.",
                        meditation = "Because of his angelic purity and burning love, the Child Jesus descended from heaven and rested in Saint Anthony's arms, filling his cell with celestial radiance.",
                        prayer = listOf(
                            "O Chaste Lily of Heaven, Blessed Anthony,",
                            "who held the Divine Child lovingly in your arms and conversed with Him face to face,",
                            "preserve my heart from all defilement and obtain for me childlike simplicity and purity before God. Amen."
                        )
                    ),
                    NovenaDay(
                        dayNumber = 7,
                        theme = "Saint Anthony, Healer of the Sick",
                        scriptureReference = "Jeremiah 17:14",
                        scriptureText = "Heal me, Lord, and I will be healed; save me and I will be saved, for you are the one I praise.",
                        intention = "For the sick, those facing surgery, and all enduring chronic physical or mental pain.",
                        meditation = "During his earthly life and throughout the centuries, thousands of miraculous cures have been granted through the touch and prayers of Saint Anthony.",
                        prayer = listOf(
                            "Compassionate Saint Anthony, physician of souls and bodies,",
                            "plead before the Divine Healer for all who are suffering under illness and infirmity.",
                            "May God grant them relief, strength, and complete healing according to His Holy Will. Amen."
                        )
                    ),
                    NovenaDay(
                        dayNumber = 8,
                        theme = "Saint Anthony, Peacemaker in Families",
                        scriptureReference = "Colossians 3:13",
                        scriptureText = "Bear with each other and forgive one another... Forgive as the Lord forgave you.",
                        intention = "For reconciliation in broken families, marital harmony, and peace in communities.",
                        meditation = "Saint Anthony traveled across Italy reconciling bitter civic enemies, estranged family members, and broken households through the peace of Christ.",
                        prayer = listOf(
                            "Apostle of Peace, Saint Anthony,",
                            "bring harmony and mutual understanding into our homes.",
                            "Melt away all resentment, silence, and anger, and bind our families together in Christ's enduring charity. Amen."
                        )
                    ),
                    NovenaDay(
                        dayNumber = 9,
                        theme = "Saint Anthony, Guide to Eternal Beatitude",
                        scriptureReference = "Revelation 21:4",
                        scriptureText = "He will wipe every tear from their eyes. There will be no more death or mourning or crying or pain.",
                        intention = "For final perseverance, the grace of a holy death, and eternal union with Christ.",
                        meditation = "On his deathbed, Saint Anthony said: 'I see my Lord!' As this novena concludes, we place all our hopes in the hands of this faithful intercessor.",
                        prayer = listOf(
                            "O Most Glorious Saint Anthony, wonder-worker and friend of all who invoke you,",
                            "as I conclude this novena, I place my petitions with total confidence before you.",
                            "Present them to the Lord Jesus, and guide my steps on earth until I join you in singing the eternal praises of God in Heaven. Amen."
                        )
                    )
                )
            )
        }
    }
}
