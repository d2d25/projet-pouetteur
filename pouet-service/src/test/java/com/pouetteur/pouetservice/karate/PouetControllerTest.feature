Feature: Test API REST PouetsController

  Background:
    * def baseUrl = 'http://localhost:3333'
    * def basePublicationsUrl = 'http://localhost:3335/api/pouets'

  Scenario: Crée un utilisateur pour les tests
    Given url baseUrl + '/auth/register'
    And request { "username" : "usertest","email" : "truc@gmail.com","password" : "testtest" }
    When method POST
    Then status 201

  Scenario: Authentification pour se connecter et manipuler des pouets
    Given url baseUrl + '/auth/login'
    And request { "login": "usertest", "password": "testtest" }
    When method POST
    Then status 200
    And def token = response.accessToken
    And def id = response.userId
    And match id != null
    And match token != null

    # Créer un pouet
    Given url basePublicationsUrl
    And request {"title" : "new test","body" : "feur feur feur feur","tags" : ["test", "lol"]}
    And header Authorization = 'Bearer ' + token
    And method post
    Then status 201
    * def pouet_created = response

    #Récupérer un pouet par son id
    Given url basePublicationsUrl + '/' + pouet_created.idPouet
    And header Authorization = 'Bearer ' + token
    And method get
    Then status 200

    #Récupérer tous les pouets
    Given url basePublicationsUrl
    And header Authorization = 'Bearer ' + token
    And method get
    Then status 200

    #Récupérer un pouet par un de ses tags
    Given url basePublicationsUrl + '/tags/0'
    And header Authorization = 'Bearer ' + token
    And method get
    Then status 200

    #Récupérer les pouets par le username de son créateur
    Given url basePublicationsUrl + '/members/' + pouet_created.author.username
    And header Authorization = 'Bearer ' + token
    And method get
    Then status 200

    #Ajouter un media a un pouet
    Given url basePublicationsUrl + '/' + pouet_created.idPouet + '/medias'
    And header Authorization = 'Bearer ' + token
    And request {"media" : "testtfbfvhbff"}
    And method patch
    Then status 200

    #Ajouter un tag a un pouet
    Given url basePublicationsUrl + '/' + pouet_created.idPouet + '/tags'
    And header Authorization = 'Bearer ' + token
    And request {"tag" : "nouveautag"}
    And method patch
    Then status 200

    #Reagir a un pouet
    Given url basePublicationsUrl + '/' + pouet_created.idPouet + '/reactions'
    And header Authorization = 'Bearer ' + token
    And request {"reaction" : "HaHa"}
    And method patch
    Then status 200

    #Récupérer le nombre total de réactions HaHa sur un pouet
    Given url basePublicationsUrl + '/' + pouet_created.idPouet + '/reactions/haha'
    And header Authorization = 'Bearer ' + token
    And method get
    Then status 200

    #Reagir a un pouet
    Given url basePublicationsUrl + '/' + pouet_created.idPouet + '/reactions'
    And header Authorization = 'Bearer ' + token
    And request {"reaction" : "Wow"}
    And method patch
    Then status 200

    #Reagir a un pouet
    Given url basePublicationsUrl + '/' + pouet_created.idPouet + '/reactions'
    And header Authorization = 'Bearer ' + token
    And request {"reaction" : "Like"}
    And method patch
    Then status 200

    #Reagir a un pouet
    Given url basePublicationsUrl + '/' + pouet_created.idPouet + '/reactions'
    And header Authorization = 'Bearer ' + token
    And request {"reaction" : "Sad"}
    And method patch
    Then status 200

    #Reagir a un pouet
    Given url basePublicationsUrl + '/' + pouet_created.idPouet + '/reactions'
    And header Authorization = 'Bearer ' + token
    And request {"reaction" : "Angry"}
    And method patch
    Then status 200

    #Récupérer le nombre total de réactions sur un pouet
    Given url basePublicationsUrl + '/' + pouet_created.idPouet + '/reactions/count'
    And header Authorization = 'Bearer ' + token
    And method get
    Then status 200

     #Récupérer le nombre total de réactions Like sur un pouet
    Given url basePublicationsUrl + '/' + pouet_created.idPouet  + '/reactions/like'
    And header Authorization = 'Bearer ' + token
    And method get
    Then status 200

     #Récupérer le nombre total de réactions Love sur un pouet
    Given url basePublicationsUrl + '/' + pouet_created.idPouet + '/reactions/love'
    And header Authorization = 'Bearer ' + token
    And method get
    Then status 200

     #Récupérer le nombre total de réactions Wow sur un pouet
    Given url basePublicationsUrl + '/' + pouet_created.idPouet + '/reactions/wow'
    And header Authorization = 'Bearer ' + token
    And method get
    Then status 200

     #Récupérer le nombre total de réactions Sad sur un pouet
    Given url basePublicationsUrl + '/' + pouet_created.idPouet + '/reactions/sad'
    And header Authorization = 'Bearer ' + token
    And method get
    Then status 200

    #Récupérer le nombre total de réactions Angry sur un pouet
    Given url basePublicationsUrl + '/' + pouet_created.idPouet + '/reactions/angry'
    And header Authorization = 'Bearer ' + token
    And method get
    Then status 200

    # Répondre à un pouet
    Given url basePublicationsUrl + '/'  + pouet_created.idPouet +'/answers'
    And request {"title" : "new test","body" : "feur feur feur feur","tags" : ["test", "lol"]}
    And header Authorization = 'Bearer ' + token
    And method post
    Then status 200


    #Partager un pouet
    Given url basePublicationsUrl + '/' + pouet_created.idPouet + '/share'
    And header Authorization = 'Bearer ' + token
    And request {"destination": {"id": 2,"username": "autreMembreConnecte"}}
    And method patch
    Then status 200

    #Récupérer le nombre total de partage d'un pouet
    Given url basePublicationsUrl + '/' + pouet_created.idPouet + '/partages'
    And header Authorization = 'Bearer ' + token
    And method get
    Then status 200


  Scenario: Authentification pour se connecter et manipuler des pouets
    Given url baseUrl + '/auth/login'
    And request { "login": "usertest", "password": "testtest" }
    When method POST
    Then status 200
    And def token = response.accessToken
    And def id = response.userId
    And match id != null
    And match token != null

    # Créer un pouet
    Given url basePublicationsUrl
    And request {"title" : "new test","body" : "feur feur feur feur","tags" : ["test", "lol"]}
    And header Authorization = 'Bearer ' + token
    And method post
    Then status 201
    * def pouet_created = response

    #Récupérer un pouet par son id
    Given url basePublicationsUrl + '/' + pouet_created.idPouet
    And header Authorization = 'Bearer ' + token
    And method get
    Then status 200

    #Récupérer tous les pouets
    Given url basePublicationsUrl
    And header Authorization = 'Bearer ' + token
    And method get
    Then status 200

    #Récupérer un pouet par un de ses tags
    Given url basePublicationsUrl + '/tags/0'
    And header Authorization = 'Bearer ' + token
    And method get
    Then status 200

    #Récupérer les pouets par le username de son créateur
    Given url basePublicationsUrl + '/members/' + pouet_created.author.username
    And header Authorization = 'Bearer ' + token
    And method get
    Then status 200

    #Ajouter un media a un pouet
    Given url basePublicationsUrl + '/' + pouet_created.idPouet + '/medias'
    And header Authorization = 'Bearer ' + token
    And request {"media" : "testtfbfvhbff"}
    And method patch
    Then status 200

    #Ajouter un tag a un pouet
    Given url basePublicationsUrl + '/' + pouet_created.idPouet + '/tags'
    And header Authorization = 'Bearer ' + token
    And request {"tag" : "nouveautag"}
    And method patch
    Then status 200

    #Reagir a un pouet
    Given url basePublicationsUrl + '/' + pouet_created.idPouet + '/reactions'
    And header Authorization = 'Bearer ' + token
    And request {"reaction" : "HaHa"}
    And method patch
    Then status 200

    #Récupérer le nombre total de réactions HaHa sur un pouet
    Given url basePublicationsUrl + '/' + pouet_created.idPouet + '/reactions/haha'
    And header Authorization = 'Bearer ' + token
    And method get
    Then status 200

    #Reagir a un pouet
    Given url basePublicationsUrl + '/' + pouet_created.idPouet + '/reactions'
    And header Authorization = 'Bearer ' + token
    And request {"reaction" : "Wow"}
    And method patch
    Then status 200

    #Reagir a un pouet
    Given url basePublicationsUrl + '/' + pouet_created.idPouet + '/reactions'
    And header Authorization = 'Bearer ' + token
    And request {"reaction" : "Like"}
    And method patch
    Then status 200

    #Reagir a un pouet
    Given url basePublicationsUrl + '/' + pouet_created.idPouet + '/reactions'
    And header Authorization = 'Bearer ' + token
    And request {"reaction" : "Sad"}
    And method patch
    Then status 200

    #Reagir a un pouet
    Given url basePublicationsUrl + '/' + pouet_created.idPouet + '/reactions'
    And header Authorization = 'Bearer ' + token
    And request {"reaction" : "Angry"}
    And method patch
    Then status 200

    #Récupérer le nombre total de réactions sur un pouet
    Given url basePublicationsUrl + '/' + pouet_created.idPouet + '/reactions/count'
    And header Authorization = 'Bearer ' + token
    And method get
    Then status 200

     #Récupérer le nombre total de réactions Like sur un pouet
    Given url basePublicationsUrl + '/' + pouet_created.idPouet  + '/reactions/like'
    And header Authorization = 'Bearer ' + token
    And method get
    Then status 200

     #Récupérer le nombre total de réactions Love sur un pouet
    Given url basePublicationsUrl + '/' + pouet_created.idPouet + '/reactions/love'
    And header Authorization = 'Bearer ' + token
    And method get
    Then status 200

     #Récupérer le nombre total de réactions Wow sur un pouet
    Given url basePublicationsUrl + '/' + pouet_created.idPouet + '/reactions/wow'
    And header Authorization = 'Bearer ' + token
    And method get
    Then status 200

     #Récupérer le nombre total de réactions Sad sur un pouet
    Given url basePublicationsUrl + '/' + pouet_created.idPouet + '/reactions/sad'
    And header Authorization = 'Bearer ' + token
    And method get
    Then status 200

    #Récupérer le nombre total de réactions Angry sur un pouet
    Given url basePublicationsUrl + '/' + pouet_created.idPouet + '/reactions/angry'
    And header Authorization = 'Bearer ' + token
    And method get
    Then status 200

    # Répondre à un pouet
    Given url basePublicationsUrl + '/'  + pouet_created.idPouet +'/answers'
    And request {"title" : "new test","body" : "feur feur feur feur","tags" : ["test", "lol"]}
    And header Authorization = 'Bearer ' + token
    And method post
    Then status 200


    #Partager un pouet
    Given url basePublicationsUrl + '/' + pouet_created.idPouet + '/share'
    And header Authorization = 'Bearer ' + token
    And request {"destination": {"id": 2,"username": "autreMembreConnecte"}}
    And method patch
    Then status 200

    #Récupérer le nombre total de partage d'un pouet
    Given url basePublicationsUrl + '/' + pouet_created.idPouet + '/partages'
    And header Authorization = 'Bearer ' + token
    And method get
    Then status 200










