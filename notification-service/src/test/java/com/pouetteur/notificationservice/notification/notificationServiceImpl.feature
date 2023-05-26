Feature: Notification Service

  Background:
    * def baseUrl = 'http://localhost:3333'
    * def baseNotificationUrl = 'http://localhost:3337/notifications'

  Scenario: Crée un utilisateur pour les tests, lancer qu'une fois sinon erreur 400 à chaque fois
    Given url baseUrl + '/auth/register'
    And request { "username": "Hugo", "email": "Hugo@gmail.com", "password": "adminadmin" }
    When method POST
    Then status 201

  Scenario: Authentification et création des notifications
    Given url baseUrl + '/auth/login'
    And request { "login": "Hugo", "password": "adminadmin" }
    When method POST
    Then status 200
    And def hugo_token = response.accessToken
    And def hugo_id = response.userId
    And match hugo_id != null
    And match hugo_token != null


    # Créer une notification de type abonnement
    Given url baseNotificationUrl
    And header Authorization = 'Bearer ' + hugo_token
    And request { "title": "Un utilisateur s'est abonné à vous", "body": "Le membre Julien s'est abonné à vous", "type": "abonnement", "notifieId": "#(hugo_id)", "notifiantMember": { "id": "789" } }
    When method POST
    Then status 201
    * def created_notification_id_abonnement = response.id

    # Créer une notification de type réaction
    Given url baseNotificationUrl
    And header Authorization = 'Bearer ' + hugo_token
    And request { "title": "Nouvelle réaction", "body": "Pouet A a été aimé", "type": "reaction", "notifieId": "#(hugo_id)", "notifiantPouet": { "idPouet": "12345", "author": { "id": "#(hugo_id)" }, "title": "Pouet A", "body": "Ceci est un pouet" } }
    When method POST
    Then status 201
    * def created_notification_id_reaction = response.id

    # Récupérer une notification abonnement par ID
    Given url baseNotificationUrl + '/' + created_notification_id_abonnement
    And header Authorization = 'Bearer ' + hugo_token
    When method GET
    Then status 200

    # Récupérer une notification reaction par ID
    Given url baseNotificationUrl + '/' + created_notification_id_reaction
    And header Authorization = 'Bearer ' + hugo_token
    When method GET
    Then status 200

    # Mise à jour d'une notification de type réaction
    Given url baseNotificationUrl + '/' + created_notification_id_reaction
    And header Authorization = 'Bearer ' + hugo_token
    And request { "title": "Nouvelle réaction", "body": "Pouet A n'ai plus aimé", "type": "reaction", "notifie": { "id": "#(hugo_id)" }, "notifiant" : { "id": "12345", "author": { "id": "#(hugo_id)" }, "title": "Pouet A", "body": "Ceci est un pouet" } }
    When method PUT
    Then status 200

    # Récupérer les notifications par notifieId
    Given url baseNotificationUrl + '/notifie/' + hugo_id
    And header Authorization = 'Bearer ' + hugo_token
    When method GET
    Then status 200

    # Supprimer une notification par ID
    Given url baseNotificationUrl + '/' + created_notification_id_abonnement
    And header Authorization = 'Bearer ' + hugo_token
    When method DELETE
    Then status 204

    # Récupérer notification abonnement par ID pour vérifier qu'elle est bien supprimée
    Given url baseNotificationUrl + '/' + created_notification_id_abonnement
    And header Authorization = 'Bearer ' + hugo_token
    When method GET
    Then status 404