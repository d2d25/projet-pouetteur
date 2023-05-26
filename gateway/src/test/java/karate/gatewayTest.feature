Feature: gateway integration tests

  Background:
    * def baseUrl = "http://localhost:8080/pouetteur/v1"
    * def authUrl = baseUrl + '/api/auth'
    * def userUrl = authUrl + '/users'
    * def profileUrl = baseUrl + '/api/profiles'
    * def messagingUrl = baseUrl + '/api/messages'
    * def pouetUrl = "http://localhost:3335" + '/api/pouets'
    * def notificationurl = baseUrl + '/api/notifications'


  Scenario: CLEAN les users
  #Suppression de Hugo
    # Login Hugo
    * print "Hugo se connecte"
    Given url authUrl + '/login'
    And request { "login": "Hugo", "password": "HugoPassword" }
    When method POST
    Then status 200
    And def token_Hugo = response.accessToken
    And def HugoId = response.userId
    And match token_Hugo != null
    And match HugoId != null
    #Suppression de Hugo
    * print "Suppression de Hugo"
    Given url userUrl + '/' + HugoId
    And header Authorization = 'Bearer ' + token_Hugo
    When method DELETE
    Then status 200
    # Login Carlos
    * print "Carlos se connecte"
    Given url authUrl + '/login'
    And request { "login": "Carlos", "password": "CarlosPassword" }
    When method POST
    Then status 200
    And def CarlosId = response.userId
    And def token_Carlos = response.accessToken
    And match token_Carlos != null
    And match CarlosId != null
  #Suppression de Carlos
    * print "Suppression de Carlos"
    Given url userUrl + '/' + CarlosId
    And header Authorization = 'Bearer ' + token_Carlos
    When method DELETE
    Then status 200
    # Login Brutus
    * print "Brutus se connecte"
    Given url authUrl + '/login'
    And request { "login": "Brutus", "password": "BrutusPassword" }
    When method POST
    Then status 200
    And def BrutusId = response.userId
    And def token_Brutus = response.accessToken
    And match token_Brutus != null
    And match BrutusId != null
  #Suppression de Brutus
    * print "Suppression de Brutus"
    Given url userUrl + '/' + BrutusId
    And header Authorization = 'Bearer ' + token_Brutus
    When method DELETE
    Then status 200

#--------------------------------------------------------------------------------------------------------------------

  Scenario: Register des utilisateurs
    #register Hugo
    * print "Hugo s'inscrit"
    Given url authUrl + "/register"
    And request { "username": "Hugo", "email": "Hugo@gmail.com", "password": "HugoPassword" }
    When method POST
    Then status 201

    #register Carlos
    * print "Carlos s'inscrit"
    Given url authUrl + "/register"
    And request { "username": "Carlos", "email": "Carlos@gmail.com", "password": "CarlosPassword" }
    When method POST
    Then status 201

    #register Brutus
    * print "Brutus s'inscrit"
    Given url authUrl + "/register"
    And request { "username": "Brutus", "email": "Brutus@gmail.com", "password": "BrutusPassword" }
    When method POST
    Then status 201

    * print "Nos 3 utilisateurs sont enregistrés"

#--------------------------------------------------------------------------------------------------------------------



  Scenario: Scénario 1 Hugo se subscribe à Carlos et Carlos se subscribe à Brutus, Hugo crée une conversation avec Carlos et envoie un message, Carlos répond et rajoute Brutus à la conversation
    ###INITIALISATION DU SCENARIO
    * print 'Scénario 1'

    ##Creation des Users

    #register Hugo
    * print "Hugo s'inscrit"
    Given url authUrl + "/register"
    And request { "username": "Hugo", "email": "Hugo@gmail.com", "password": "HugoPassword" }
    When method POST
    Then status 201

    #register Carlos
    * print "Carlos s'inscrit"
    Given url authUrl + "/register"
    And request { "username": "Carlos", "email": "Carlos@gmail.com", "password": "CarlosPassword" }
    When method POST
    Then status 201

    #register Brutus
    * print "Brutus s'inscrit"
    Given url authUrl + "/register"
    And request { "username": "Brutus", "email": "Brutus@gmail.com", "password": "BrutusPassword" }
    When method POST
    Then status 201

    ##Login des User et initialisation des Tokens et Ids
    # Login Hugo
    * print "Hugo se connecte"
    Given url authUrl + '/login'
    And request { "login": "Hugo", "password": "HugoPassword" }
    When method POST
    Then status 200
    And def token_Hugo = response.accessToken
    And def HugoId = response.userId
    And match token_Hugo != null
    And match HugoId != null

    # Login Carlos
    * print "Carlos se connecte"
    Given url authUrl + '/login'
    And request { "login": "Carlos", "password": "CarlosPassword" }
    When method POST
    Then status 200
    And def CarlosId = response.userId
    And def token_Carlos = response.accessToken
    And match token_Carlos != null
    And match CarlosId != null

    # Login Brutus
    * print "Brutus se connecte"
    Given url authUrl + '/login'
    And request { "login": "Brutus", "password": "BrutusPassword" }
    When method POST
    Then status 200
    And def BrutusId = response.userId
    And def token_Brutus = response.accessToken
    And match token_Brutus != null
    And match BrutusId != null


    ### DEBUT DES TESTS

    ## Test des de recuperer des profils

    #Exemple getProfileById avec l'id de Brutus
    * print "Brutus souhaite récupérer les informations de son profile"
    Given url profileUrl + '/' + BrutusId
    And header Authorization = 'Bearer ' + token_Brutus
    When method GET
    Then status 200
    And match response.id == BrutusId
    And match response.username == 'Brutus'

    ## Test de follow des profiles

    # Hugo se subscribe à Carlos
    * print "Hugo se met a follow Carlos"
    Given url profileUrl + '/subscribe/' + CarlosId
    And header Authorization = 'Bearer ' + token_Hugo
    When method POST
    Then status 200
    # Carlos se subscribe à Brutus
    * print "Carlos se met a follow Brutus"
    Given url profileUrl + '/subscribe/' + BrutusId
    And header Authorization = 'Bearer ' + token_Carlos
    When method POST
    Then status 200




    ###CLEAN DES UTILISATEUR

    * print "Suppression des utilisateurs"
    #Suppression de Brutus
    * print "Suppression de Brutus"
    Given url userUrl + '/' + BrutusId
    And header Authorization = 'Bearer ' + token_Brutus
    When method DELETE
    Then status 200
    #Suppression de Carlos
    * print "Suppression de Carlos"
    Given url userUrl + '/' + CarlosId
    And header Authorization = 'Bearer ' + token_Carlos
    When method DELETE
    Then status 200
    #Suppression de Hugo
    * print "Suppression de Hugo"
    Given url userUrl + '/' + HugoId
    And header Authorization = 'Bearer ' + token_Hugo
    When method DELETE
    Then status 200

    * print "Fin scénario 1"
    ###FIN SCENARIO 1

  #--------------------------------------------------------------------------------------------------------------------


#    Scenario: Hugo se connecte et envoie un nouveau Pouet
#    #Login Hugo
#    Given url baseUrl + '/auth/login'
#    And request { "login": "Hugo", "password": "HugoPassword" }
#    When method POST
#    Then status 200
#    And def token_Hugo = response.accessToken
#    And def HugoId = response.userId
#    And match token_Hugo != null
#    And match HugoId != null
#    #Hugo crée un nouveau Pouet
#    Given url pouetUrl + 'pouets'


  Scenario: Hugo crée une conversation avec Carlos, il envoie un message, Carlos lui repond puis rajoute Brutus à la conversation
    # Login Hugo
    * print "Hugo se connecte"
    Given url authUrl + '/login'
    And request { "login": "Hugo", "password": "HugoPassword" }
    When method POST
    Then status 200
    And def token_Hugo = response.accessToken
    And def HugoId = response.userId
    And match token_Hugo != null
    And match HugoId != null
    # Login Carlos
    * print "Carlos se connecte"
    Given url authUrl + '/login'
    And request { "login": "Carlos", "password": "CarlosPassword" }
    When method POST
    Then status 200
    And def CarlosId = response.userId
    And def token_Carlos = response.accessToken
    And match token_Carlos != null
    And match CarlosId != null
    # Login Brutus
    * print "Brutus se connecte"
    Given url authUrl + '/login'
    And request { "login": "Brutus", "password": "BrutusPassword" }
    When method POST
    Then status 200
    And def BrutusId = response.userId
    And def token_Brutus = response.accessToken
    And match token_Brutus != null
    And match BrutusId != null

    # Hugo crée une conversation avec Carlos
    * print "Hugo crée une conversation avec Carlos"
    Given url messagingUrl + '/conversation'
    And header Authorization = 'Bearer ' + token_Hugo
    And request {"name":"pouet","members":[{"id":"#(HugoId)","username":"Hugo"}, {"id":"#(CarlosId)","username":"Carlos"}]}
    When method POST
    Then status 201
    * def conversationId = response.id
    And match conversationId != null
    And match response.photo == 'user.png'
    And match response.members == '#[2]'
    And match response.messages == '#[0]'

    # Hugo envoie un message à Carlos
    * print "Hugo envoie un message dans la conversation"
    Given url messagingUrl + '/' + conversationId + '/sendMessage'
    And header Authorization = 'Bearer ' + token_Hugo
    And param text = "salut Carlos, peux-tu rajouter Brutus à la conversation ?"
    When method POST
    Then status 200
    And match response.messages == "#[1]"
    And match response.messages[0].author.id == HugoId
    And match response.messages[0].body == "salut Carlos, peux-tu rajouter Brutus à la conversation ?"

    # Carlos repond à Hugos
    * print "Carlos repond dans la conversation"
    Given url messagingUrl + '/' + conversationId + '/sendMessage'
    And header Authorization = 'Bearer ' + token_Carlos
    And param text = "Ok, je m'en occupe !"
    When method POST
    Then status 200
    And match response.messages == "#[2]"
    And match response.messages[1].author.id == CarlosId
    And match response.messages[1].body == "Ok, je m'en occupe !"

    #Carlos rajoute Brutus à la conversation
    Given url messagingUrl + '/' + conversationId + '/addMembers'
    And header Authorization = 'Bearer ' + token_Carlos
    And request [{"id":"#(BrutusId)","username":"Brutus"}]
    When method PUT
    Then status 200
    And match response.members == '#[3]'

    Scenario: Hugo, Carlos et Brutus n'ont plus besoin de la conversation, ils la quittent, cette dernière ce supprime automatiquement
      # Login Hugo
      * print "Hugo se connecte"
      Given url authUrl + '/login'
      And request { "login": "Hugo", "password": "HugoPassword" }
      When method POST
      Then status 200
      And def token_Hugo = response.accessToken
      And def HugoId = response.userId
      And match token_Hugo != null
      And match HugoId != null
      # Login Carlos
      * print "Carlos se connecte"
      Given url authUrl + '/login'
      And request { "login": "Carlos", "password": "CarlosPassword" }
      When method POST
      Then status 200
      And def CarlosId = response.userId
      And def token_Carlos = response.accessToken
      And match token_Carlos != null
      And match CarlosId != null
      # Login Brutus
      * print "Brutus se connecte"
      Given url authUrl + '/login'
      And request { "login": "Brutus", "password": "BrutusPassword" }
      When method POST
      Then status 200
      And def BrutusId = response.userId
      And def token_Brutus = response.accessToken
      And match token_Brutus != null
      And match BrutusId != null

      # Recréation de la conversation
      * print "Hugo crée une conversation avec Carlos"
      Given url messagingUrl + '/conversation'
      And header Authorization = 'Bearer ' + token_Hugo
      And request {"name":"pouet","members":[{"id":"#(HugoId)","username":"Hugo"}, {"id":"#(CarlosId)","username":"Carlos"}, {"id":"#(BrutusId)","username":"Brutus"}]}
      When method POST
      Then status 201
      * def conversationId = response.id
      And match conversationId != null
      And match response.photo == 'default.png'
      And match response.members == '#[3]'
      And match response.messages == '#[0]'
      Given url messagingUrl + '/' + conversationId + '/leaveConversation'

      #leave conversation
      * print "Hugo quitte la conversation"
      Given url messagingUrl + '/' + conversationId + '/leaveConversation'
      And header Authorization = 'Bearer ' + token_Hugo
      When method PUT
      Then status 200
      * print "Carlos quitte la conversation"
      Given url messagingUrl + '/' + conversationId + '/leaveConversation'
      And header Authorization = 'Bearer ' + token_Carlos
      When method PUT
      Then status 200
      * print "La conversation se supprime automatiquement"
      #Get conversation
      * print "Verification que la conversation n'existe plus"
      Given url messagingUrl + '/' + conversationId
      And header Authorization = 'Bearer ' + token_Hugo
      When method GET
      Then status 404


  Scenario: Hugo envoie un pouet et récupère son pouet
      # Login Hugo
      * print "Hugo se connecte"
      Given url authUrl + '/login'
      And request { "login": "Hugo", "password": "HugoPassword" }
      When method POST
      Then status 200
      And def HugoId = response.userId
      And def token_Hugo = response.accessToken
      And match HugoId != null
      And match token_Hugo != null

      # Login Carlos
      * print "Carlos se connecte"
      Given url authUrl + '/login'
      And request { "login": "Carlos", "password": "CarlosPassword" }
      When method POST
      Then status 200
      And def CarlosId = response.userId
      And def token_Carlos = response.accessToken
      And match token_Carlos != null
      And match CarlosId != null


      # Créer un pouet Hugo N1
      Given url pouetUrl
      And request {"title" : "New pouet n1","body" : "Je pouet tout le temps","tags" : ["test", "lol"]}
      And header Authorization = 'Bearer ' + token_Hugo
      And method post
      Then status 201
      * def pouet_createdA = response

      # Créer un pouet Hugo N2
      Given url pouetUrl
      And request {"title" : "New pouet n2","body" : "Je ne pouet plus","tags" : ["test", "lol"]}
      And header Authorization = 'Bearer ' + token_Hugo
      And method post
      Then status 201
      * def pouet_createdB = response

      # Créer un pouet Carlos N1
      Given url pouetUrl
      And request {"title" : "New pouet n1","body" : "Je suis Carlos","tags" : ["test", "lol"]}
      And header Authorization = 'Bearer ' + token_Carlos
      And method post
      Then status 201
      * def pouet_createdC = response

      #Récupérer un pouet par son id
      Given url pouetUrl + '/' + pouet_createdA.idPouet
      And header Authorization = 'Bearer ' + token_Hugo
      And method get
      Then status 200

      #Récupérer tous les pouets
      Given url pouetUrl
      And header Authorization = 'Bearer ' + token_Hugo
      And method get
      Then status 200

      #Récupérer un pouet par un de ses tags
      Given url pouetUrl + '/tags/0'
      And header Authorization = 'Bearer ' + token_Carlos
      And method get
      Then status 200

      #Récupérer les pouets par le username de son créateur
      Given url pouetUrl + '/members/' + pouet_createdC.author.username
      And header Authorization = 'Bearer ' + token_Carlos
      And method get
      Then status 200