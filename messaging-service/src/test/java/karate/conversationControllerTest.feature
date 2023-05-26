Feature: conversationControllerTest

  Background:
    * def authUrl = 'http://localhost:3333/auth/login'
    * def messagingUrl = 'http://localhost:3336/messaging'


  Scenario: first hello world
    * print 'hello'

  Scenario: register Brutus
    Given url "http://localhost:3333/auth/register"
    And request { "username": "Brutus", "email": "Brutus@gmail.com", "password": "password" }
    When method POST
    Then status 201

    Scenario: register Carlos
    Given url "http://localhost:3333/auth/register"
    And request { "username": "Carlos", "email": "Carlos@gmail.com", "password": "password" }
    When method POST
    Then status 201

  Scenario: creation ConversationController - 200 Success
    #Login
    Given url authUrl
    And request { "login": "Brutus", "password": "password" }
    When method POST
    Then status 200
    And def token = response.accessToken
    And def userId = response.userId
    And match userId != null
    And match token != null

    #Create conversation
    Given url messagingUrl + '/conversation'
    And header Authorization = 'Bearer ' + token
    And request {"name":"pouet","members":[{"id":"#(userId)","username":"Brutus"},{"id":"11111","username":"leo"}, {"id":"22222","username":"timothe"}]}
    When method POST
    Then status 201
    * def conversationId = response.id
    And match conversationId != null
    And match response.name == 'pouet'
    And match response.photo == 'default.png'
    And match response.members == '#[3]'
    And match response.messages == '#[0]'

  Scenario: creation conversation - 200 Success (2 members only to see if photo works)
    #Login
    Given url authUrl
    And request { "login": "Brutus", "password": "password" }
    When method POST
    Then status 200
    And def token = response.accessToken
    And def userId = response.userId
    And match userId != null
    And match token != null

    #Create conversation
    Given url messagingUrl + '/conversation'
    And header Authorization = 'Bearer ' + token
    And request {"name":"pouet","members":[{"id":"#(userId)","username":"Brutus"},{"id":"11111","username":"leo"}]}
    When method POST
    Then status 201
    * def conversationId = response.id
    And match conversationId != null
    And match response.name == 'pouet'
    And match response.photo == 'user.png'
    And match response.members == '#[2]'
    And match response.messages == '#[0]'

  Scenario: creation conversation - 200 Success (no name renseigned)
    #Login
    Given url authUrl
    And request { "login": "Brutus", "password": "password" }
    When method POST
    Then status 200
    And def token = response.accessToken
    And def userId = response.userId
    And match userId != null
    And match token != null

    #Create conversation
    Given url messagingUrl + '/conversation'
    And header Authorization = 'Bearer ' + token
    And request {"name":"","members":[{"id":"#(userId)","username":"Brutus"},{"id":"11111","username":"leo"}, {"id":"22222","username":"timothe"}]}
    When method POST
    Then status 201
    * def conversationId = response.id
    And match conversationId != null
    And match response.photo == 'default.png'
    And match response.members == '#[3]'
    And match response.messages == '#[0]'


  Scenario: creation conversation - 400 (less than 2 members)
    #Login
    Given url authUrl
    And request { "login": "Brutus", "password": "password" }
    When method POST
    Then status 200
    And def token = response.accessToken
    And def userId = response.userId
    And match userId != null
    And match token != null
    #Create conversation
    Given url messagingUrl + '/conversation'
    And header Authorization = 'Bearer ' + token
    And request {"name":"pouet","members":[{"id":"#(userId)","username":"Brutus"}]}
    When method POST
    Then status 400

  Scenario: creation conversation - 400 (author not in members list)
    #Login
    Given url authUrl
    And request { "login": "Brutus", "password": "password" }
    When method POST
    Then status 200
    And def token = response.accessToken
    And def userId = response.userId
    And match userId != null
    And match token != null
    #Create conversation
    Given url messagingUrl + '/conversation'
    And header Authorization = 'Bearer ' + token
    And request {"name":"","members":[{"id":"11111","username":"leo"}, {"id":"22222","username":"timothe"}]}
    When method POST
    Then status 400

  Scenario: creation conversation - 400 (conversation not renseigned)
    #Login
    Given url authUrl
    And request { "login": "Brutus", "password": "password" }
    When method POST
    Then status 200
    And def token = response.accessToken
    And def userId = response.userId
    And match userId != null
    And match token != null
    #Create conversation
    Given url messagingUrl + '/conversation'
    And header Authorization = 'Bearer ' + token
    When method POST
    Then status 400

  Scenario: get conversation - 200 Success
    #Login
    Given url authUrl
    And request { "login": "Brutus", "password": "password" }
    When method POST
    Then status 200
    And def token = response.accessToken
    And def userId = response.userId
    And match userId != null
    And match token != null
    #Create conversation
    Given url messagingUrl + '/conversation'
    And header Authorization = 'Bearer ' + token
    And request {"name":"pouet","members":[{"id":"#(userId)","username":"Brutus"},{"id":"11111","username":"leo"}, {"id":"22222","username":"timothe"}]}
    When method POST
    Then status 201
    * def conversationId = response.id
    And match conversationId != null
    And match response.photo == 'default.png'
    And match response.members == '#[3]'
    And match response.messages == '#[0]'
    #Get conversation
    Given url messagingUrl + '/' + conversationId
    And header Authorization = 'Bearer ' + token
    When method GET
    Then status 200
    And match response.id == conversationId
    And match response.name == 'pouet'

  Scenario: get conversations - 200 Success
    #Login
    Given url authUrl
    And request { "login": "Brutus", "password": "password" }
    When method POST
    Then status 200
    And def token = response.accessToken
    And def userId = response.userId
    And match userId != null
    And match token != null
    #Create conversation
    Given url messagingUrl + '/conversation'
    And header Authorization = 'Bearer ' + token
    And request {"name":"pouet","members":[{"id":"#(userId)","username":"Brutus"},{"id":"11111","username":"leo"}, {"id":"22222","username":"timothe"}]}
    When method POST
    Then status 201
    * def conversationId = response.id
    And match conversationId != null
    And match response.photo == 'default.png'
    And match response.members == '#[3]'
    And match response.messages == '#[0]'
    #Get conversation
    Given url messagingUrl + '/conversations'
    And header Authorization = 'Bearer ' + token
    When method GET
    Then status 200

  Scenario: get conversation - 404 conversation not found
    #Login
    Given url authUrl
    And request { "login": "Brutus", "password": "password" }
    When method POST
    Then status 200
    And def token = response.accessToken
    And def userId = response.userId
    And match userId != null
    And match token != null
    #Create conversation
    Given url messagingUrl + '/conversation'
    And header Authorization = 'Bearer ' + token
    And request {"name":"pouet","members":[{"id":"#(userId)","username":"Brutus"},{"id":"11111","username":"leo"}, {"id":"22222","username":"timothe"}]}
    When method POST
    Then status 201
    * def conversationId = response.id
    And match conversationId != null
    And match response.photo == 'default.png'
    And match response.members == '#[3]'
    And match response.messages == '#[0]'
    #Get conversation
    Given url messagingUrl + '/' + "WrongId"
    And header Authorization = 'Bearer ' + token
    When method GET
    Then status 404

  Scenario: get conversation - 400 not in conversation
    #Login
    Given url authUrl
    And request { "login": "Brutus", "password": "password" }
    When method POST
    Then status 200
    And def token = response.accessToken
    And def userId = response.userId
    And match userId != null
    And match token != null
    #Create conversation
    Given url messagingUrl + '/conversation'
    And header Authorization = 'Bearer ' + token
    And request {"name":"pouet","members":[{"id":"#(userId)","username":"Brutus"},{"id":"11111","username":"leo"}, {"id":"22222","username":"timothe"}]}
    When method POST
    Then status 201
    * def conversationId = response.id
    And match conversationId != null
    And match response.photo == 'default.png'
    And match response.members == '#[3]'
    And match response.messages == '#[0]'
    #Login
    Given url authUrl
    And request { "login": "Carlos", "password": "password" }
    When method POST
    Then status 200
    And def token_carlos = response.accessToken
    And def userId_carlos = response.userId
    And match userId_carlos != null
    And match token_carlos != null
    #Get conversation
    Given url messagingUrl + '/' + conversationId
    And header Authorization = 'Bearer ' + token_carlos
    When method GET
    Then status 400

  Scenario: add members - 200 success (add 1 member)
    #Login
    Given url authUrl
    And request { "login": "Brutus", "password": "password" }
    When method POST
    Then status 200
    And def token = response.accessToken
    And def userId = response.userId
    And match userId != null
    And match token != null
    #Create conversation
    Given url messagingUrl + '/conversation'
    And header Authorization = 'Bearer ' + token
    And request {"name":"pouet","members":[{"id":"#(userId)","username":"Brutus"},{"id":"11111","username":"leo"}, {"id":"22222","username":"timothe"}]}
    When method POST
    Then status 201
    * def conversationId = response.id
    And match conversationId != null
    And match response.photo == 'default.png'
    And match response.members == '#[3]'
    And match response.messages == '#[0]'
    #add members
    Given url messagingUrl + '/' + conversationId + '/addMembers'
    And header Authorization = 'Bearer ' + token
    And request [{"id":"33333","username":"julien"}]
    When method PUT
    Then status 200
    And match response.members == '#[4]'

  Scenario: add members - 200 success (add 2 members)
    #Login
    Given url authUrl
    And request { "login": "Brutus", "password": "password" }
    When method POST
    Then status 200
    And def token = response.accessToken
    And def userId = response.userId
    And match userId != null
    And match token != null
    #Create conversation
    Given url messagingUrl + '/conversation'
    And header Authorization = 'Bearer ' + token
    And request {"name":"pouet","members":[{"id":"#(userId)","username":"Brutus"},{"id":"11111","username":"leo"}, {"id":"22222","username":"timothe"}]}
    When method POST
    Then status 201
    * def conversationId = response.id
    And match conversationId != null
    And match response.photo == 'default.png'
    And match response.members == '#[3]'
    And match response.messages == '#[0]'
    #add members
    Given url messagingUrl + '/' + conversationId + '/addMembers'
    And header Authorization = 'Bearer ' + token
    And request [{"id":"33333","username":"julien"}, {"id":"44444","username":"denez"}]
    When method PUT
    Then status 200
    And match response.members == '#[5]'

  Scenario: add members - 404 conversation not found
    #Login
    Given url authUrl
    And request { "login": "Brutus", "password": "password" }
    When method POST
    Then status 200
    And def token = response.accessToken
    And def userId = response.userId
    And match userId != null
    And match token != null
    #Create conversation
    Given url messagingUrl + '/conversation'
    And header Authorization = 'Bearer ' + token
    And request {"name":"pouet","members":[{"id":"#(userId)","username":"Brutus"},{"id":"11111","username":"leo"}, {"id":"22222","username":"timothe"}]}
    When method POST
    Then status 201
    * def conversationId = response.id
    And match conversationId != null
    And match response.photo == 'default.png'
    And match response.members == '#[3]'
    And match response.messages == '#[0]'
    #add members
    Given url messagingUrl + '/' + "wrongId" + '/addMembers'
    And header Authorization = 'Bearer ' + token
    And request [{"id":"33333","username":"julien"}, {"id":"44444","username":"denez"}]
    When method PUT
    Then status 404

  Scenario: add members - 400 no members renseigned
    #Login
    Given url authUrl
    And request { "login": "Brutus", "password": "password" }
    When method POST
    Then status 200
    And def token = response.accessToken
    And def userId = response.userId
    And match userId != null
    And match token != null
    #Create conversation
    Given url messagingUrl + '/conversation'
    And header Authorization = 'Bearer ' + token
    And request {"name":"pouet","members":[{"id":"#(userId)","username":"Brutus"},{"id":"11111","username":"leo"}, {"id":"22222","username":"timothe"}]}
    When method POST
    Then status 201
    * def conversationId = response.id
    And match conversationId != null
    And match response.photo == 'default.png'
    And match response.members == '#[3]'
    And match response.messages == '#[0]'
    #add members
    Given url messagingUrl + '/' + conversationId + '/addMembers'
    And header Authorization = 'Bearer ' + token
    When method PUT
    Then status 400

  Scenario: add members - 400 members already in conversation
    #Login
    Given url authUrl
    And request { "login": "Brutus", "password": "password" }
    When method POST
    Then status 200
    And def token = response.accessToken
    And def userId = response.userId
    And match userId != null
    And match token != null
    #Create conversation
    Given url messagingUrl + '/conversation'
    And header Authorization = 'Bearer ' + token
    And request {"name":"pouet","members":[{"id":"#(userId)","username":"Brutus"},{"id":"11111","username":"leo"}, {"id":"22222","username":"timothe"}]}
    When method POST
    Then status 201
    * def conversationId = response.id
    And match conversationId != null
    And match response.photo == 'default.png'
    And match response.members == '#[3]'
    And match response.messages == '#[0]'
    #add members
    Given url messagingUrl + '/' + conversationId + '/addMembers'
    And header Authorization = 'Bearer ' + token
    And request [{"id":"#(userId)","username":"Brutus"}, {"id":"44444","username":"denez"}]
    When method PUT
    Then status 400

  Scenario: add members - 400 not in conversation
    #Login
    Given url authUrl
    And request { "login": "Brutus", "password": "password" }
    When method POST
    Then status 200
    And def token = response.accessToken
    And def userId = response.userId
    And match userId != null
    And match token != null
    #Create conversation
    Given url messagingUrl + '/conversation'
    And header Authorization = 'Bearer ' + token
    And request {"name":"pouet","members":[{"id":"#(userId)","username":"Brutus"},{"id":"11111","username":"leo"}, {"id":"22222","username":"timothe"}]}
    When method POST
    Then status 201
    * def conversationId = response.id
    And match conversationId != null
    And match response.photo == 'default.png'
    And match response.members == '#[3]'
    And match response.messages == '#[0]'
    #Login
    Given url authUrl
    And request { "login": "Carlos", "password": "password" }
    When method POST
    Then status 200
    And def token_carlos = response.accessToken
    And def userId_carlos = response.userId
    And match userId_carlos != null
    And match token_carlos != null
    #add members
    Given url messagingUrl + '/' + conversationId + '/addMembers'
    And header Authorization = 'Bearer ' + token_carlos
    And request [{"id":"44444","username":"denez"}]
    When method PUT
    Then status 400

  Scenario: edit name - 200 success
    #Login
    Given url authUrl
    And request { "login": "Brutus", "password": "password" }
    When method POST
    Then status 200
    And def token = response.accessToken
    And def userId = response.userId
    And match userId != null
    And match token != null
    #Create conversation
    Given url messagingUrl + '/conversation'
    And header Authorization = 'Bearer ' + token
    And request {"name":"pouet","members":[{"id":"#(userId)","username":"Brutus"},{"id":"11111","username":"leo"}, {"id":"22222","username":"timothe"}]}
    When method POST
    Then status 201
    * def conversationId = response.id
    And match conversationId != null
    And match response.photo == 'default.png'
    And match response.members == '#[3]'
    And match response.messages == '#[0]'
    #edit name
    Given url messagingUrl + '/' + conversationId + '/editName'
    And header Authorization = 'Bearer ' + token
    And param name = 'newName'
    When method PUT
    Then status 200
    And match response.name == 'newName'

  Scenario: edit name - 404 conversation not found
    #Login
    Given url authUrl
    And request { "login": "Brutus", "password": "password" }
    When method POST
    Then status 200
    And def token = response.accessToken
    And def userId = response.userId
    And match userId != null
    And match token != null
    #Create conversation
    Given url messagingUrl + '/conversation'
    And header Authorization = 'Bearer ' + token
    And request {"name":"pouet","members":[{"id":"#(userId)","username":"Brutus"},{"id":"11111","username":"leo"}, {"id":"22222","username":"timothe"}]}
    When method POST
    Then status 201
    * def conversationId = response.id
    And match conversationId != null
    And match response.photo == 'default.png'
    And match response.members == '#[3]'
    And match response.messages == '#[0]'
    #edit name
    Given url messagingUrl + '/' + "wrongId" + '/editName'
    And header Authorization = 'Bearer ' + token
    And param name = 'newName'
    When method PUT
    Then status 404

  Scenario: edit name - 400 no name renseigned
    #Login
    Given url authUrl
    And request { "login": "Brutus", "password": "password" }
    When method POST
    Then status 200
    And def token = response.accessToken
    And def userId = response.userId
    And match userId != null
    And match token != null
    #Create conversation
    Given url messagingUrl + '/conversation'
    And header Authorization = 'Bearer ' + token
    And request {"name":"pouet","members":[{"id":"#(userId)","username":"Brutus"},{"id":"11111","username":"leo"}, {"id":"22222","username":"timothe"}]}
    When method POST
    Then status 201
    * def conversationId = response.id
    And match conversationId != null
    And match response.photo == 'default.png'
    And match response.members == '#[3]'
    And match response.messages == '#[0]'
    #edit name
    Given url messagingUrl + '/' + conversationId + '/editName'
    And header Authorization = 'Bearer ' + token
    When method PUT
    Then status 400

  Scenario: edit name - 400 not in conversation
    #Login
    Given url authUrl
    And request { "login": "Brutus", "password": "password" }
    When method POST
    Then status 200
    And def token = response.accessToken
    And def userId = response.userId
    And match userId != null
    And match token != null
    #Create conversation
    Given url messagingUrl + '/conversation'
    And header Authorization = 'Bearer ' + token
    And request {"name":"pouet","members":[{"id":"#(userId)","username":"Brutus"},{"id":"11111","username":"leo"}, {"id":"22222","username":"timothe"}]}
    When method POST
    Then status 201
    * def conversationId = response.id
    And match conversationId != null
    And match response.photo == 'default.png'
    And match response.members == '#[3]'
    And match response.messages == '#[0]'
    #Login
    Given url authUrl
    And request { "login": "Carlos", "password": "password" }
    When method POST
    Then status 200
    And def token_carlos = response.accessToken
    And def userId_carlos = response.userId
    And match userId_carlos != null
    And match token_carlos != null
    #edit name
    Given url messagingUrl + '/' + conversationId + '/editName'
    And header Authorization = 'Bearer ' + token_carlos
    And param name = 'newName'
    When method PUT
    Then status 400

  Scenario: edit photo - 200 success
    #Login
    Given url authUrl
    And request { "login": "Brutus", "password": "password" }
    When method POST
    Then status 200
    And def token = response.accessToken
    And def userId = response.userId
    And match userId != null
    And match token != null
    #Create conversation
    Given url messagingUrl + '/conversation'
    And header Authorization = 'Bearer ' + token
    And request {"name":"pouet","members":[{"id":"#(userId)","username":"Brutus"},{"id":"11111","username":"leo"}, {"id":"22222","username":"timothe"}]}
    When method POST
    Then status 201
    * def conversationId = response.id
    And match conversationId != null
    And match response.photo == 'default.png'
    And match response.members == '#[3]'
    And match response.messages == '#[0]'
    #edit photo
    Given url messagingUrl + '/' + conversationId + '/editPhoto'
    And header Authorization = 'Bearer ' + token
    And param photo = 'newPhoto.png'
    When method PUT
    Then status 200

  Scenario: edit photo - 404 conversation not found
    #Login
    Given url authUrl
    And request { "login": "Brutus", "password": "password" }
    When method POST
    Then status 200
    And def token = response.accessToken
    And def userId = response.userId
    And match userId != null
    And match token != null
    #Create conversation
    Given url messagingUrl + '/conversation'
    And header Authorization = 'Bearer ' + token
    And request {"name":"pouet","members":[{"id":"#(userId)","username":"Brutus"},{"id":"11111","username":"leo"}, {"id":"22222","username":"timothe"}]}
    When method POST
    Then status 201
    * def conversationId = response.id
    And match conversationId != null
    And match response.photo == 'default.png'
    And match response.members == '#[3]'
    And match response.messages == '#[0]'
    #edit photo
    Given url messagingUrl + '/' + "etgytrg" + '/editPhoto'
    And header Authorization = 'Bearer ' + token
    And param photo = 'newPhoto.png'
    When method PUT
    Then status 404

  Scenario: edit photo - 400 no photo renseigned
    #Login
    Given url authUrl
    And request { "login": "Brutus", "password": "password" }
    When method POST
    Then status 200
    And def token = response.accessToken
    And def userId = response.userId
    And match userId != null
    And match token != null
    #Create conversation
    Given url messagingUrl + '/conversation'
    And header Authorization = 'Bearer ' + token
    And request {"name":"pouet","members":[{"id":"#(userId)","username":"Brutus"},{"id":"11111","username":"leo"}, {"id":"22222","username":"timothe"}]}
    When method POST
    Then status 201
    * def conversationId = response.id
    And match conversationId != null
    And match response.photo == 'default.png'
    And match response.members == '#[3]'
    And match response.messages == '#[0]'
    #edit photo
    Given url messagingUrl + '/' + conversationId + '/editPhoto'
    And header Authorization = 'Bearer ' + token
    When method PUT
    Then status 400

  Scenario: add members - 400 not in conversation
    #Login
    Given url authUrl
    And request { "login": "Brutus", "password": "password" }
    When method POST
    Then status 200
    And def token = response.accessToken
    And def userId = response.userId
    And match userId != null
    And match token != null
    #Create conversation
    Given url messagingUrl + '/conversation'
    And header Authorization = 'Bearer ' + token
    And request {"name":"pouet","members":[{"id":"#(userId)","username":"Brutus"},{"id":"11111","username":"leo"}, {"id":"22222","username":"timothe"}]}
    When method POST
    Then status 201
    * def conversationId = response.id
    And match conversationId != null
    And match response.photo == 'default.png'
    And match response.members == '#[3]'
    And match response.messages == '#[0]'
    #Login
    Given url authUrl
    And request { "login": "Carlos", "password": "password" }
    When method POST
    Then status 200
    And def token_carlos = response.accessToken
    And def userId_carlos = response.userId
    And match userId_carlos != null
    And match token_carlos != null
    #edit photo
    Given url messagingUrl + '/' + conversationId + '/editPhoto'
    And header Authorization = 'Bearer ' + token_carlos
    And param name = 'newPhoto.png'
    When method PUT
    Then status 400

  Scenario: leave conversation - 200 success
    #Login
    Given url authUrl
    And request { "login": "Brutus", "password": "password" }
    When method POST
    Then status 200
    And def token = response.accessToken
    And def userId = response.userId
    And match userId != null
    And match token != null
    #Create conversation
    Given url messagingUrl + '/conversation'
    And header Authorization = 'Bearer ' + token
    And request {"name":"pouet","members":[{"id":"#(userId)","username":"Brutus"},{"id":"11111","username":"leo"}, {"id":"22222","username":"timothe"}]}
    When method POST
    Then status 201
    * def conversationId = response.id
    And match conversationId != null
    And match response.photo == 'default.png'
    And match response.members == '#[3]'
    And match response.messages == '#[0]'
    #leave conversation
    Given url messagingUrl + '/' + conversationId + '/leaveConversation'
    And header Authorization = 'Bearer ' + token
    When method PUT
    Then status 200


  Scenario: leave conversation - 200 success (conversation is deleted)
    #Login
    Given url authUrl
    And request { "login": "Brutus", "password": "password" }
    When method POST
    Then status 200
    And def token = response.accessToken
    And def userId = response.userId
    And match userId != null
    And match token != null
    #Create conversation
    Given url messagingUrl + '/conversation'
    And header Authorization = 'Bearer ' + token
    And request {"name":"pouet","members":[{"id":"#(userId)","username":"Brutus"},{"id":"11111","username":"leo"}]}
    When method POST
    Then status 201
    * def conversationId = response.id
    And match conversationId != null
    And match response.photo == 'user.png'
    And match response.members == '#[2]'
    And match response.messages == '#[0]'
    #leave conversation
    Given url messagingUrl + '/' + conversationId + '/leaveConversation'
    And header Authorization = 'Bearer ' + token
    When method PUT
    Then status 200

  Scenario: leave conversation - 404 conversation not found
    #Login
    Given url authUrl
    And request { "login": "Brutus", "password": "password" }
    When method POST
    Then status 200
    And def token = response.accessToken
    And def userId = response.userId
    And match userId != null
    And match token != null
    #Create conversation
    Given url messagingUrl + '/conversation'
    And header Authorization = 'Bearer ' + token
    And request {"name":"pouet","members":[{"id":"#(userId)","username":"Brutus"},{"id":"11111","username":"leo"}, {"id":"22222","username":"timothe"}]}
    When method POST
    Then status 201
    * def conversationId = response.id
    And match conversationId != null
    And match response.photo == 'default.png'
    And match response.members == '#[3]'
    And match response.messages == '#[0]'
    #leave conversation
    Given url messagingUrl + '/' + "wrongId" + '/leaveConversation'
    And header Authorization = 'Bearer ' + token
    When method PUT
    Then status 404

  Scenario: leave conversation - 400 not in conversation
    #Login
    Given url authUrl
    And request { "login": "Brutus", "password": "password" }
    When method POST
    Then status 200
    And def token = response.accessToken
    And def userId = response.userId
    And match userId != null
    And match token != null
    #Create conversation
    Given url messagingUrl + '/conversation'
    And header Authorization = 'Bearer ' + token
    And request {"name":"pouet","members":[{"id":"#(userId)","username":"Brutus"},{"id":"11111","username":"leo"}, {"id":"22222","username":"timothe"}]}
    When method POST
    Then status 201
    * def conversationId = response.id
    And match conversationId != null
    And match response.photo == 'default.png'
    And match response.members == '#[3]'
    And match response.messages == '#[0]'
    #Login
    Given url authUrl
    And request { "login": "Carlos", "password": "password" }
    When method POST
    Then status 200
    And def token_carlos = response.accessToken
    And def userId_carlos = response.userId
    And match userId_carlos != null
    And match token_carlos != null
    #leave conversation
    Given url messagingUrl + '/' + conversationId + '/leaveConversation'
    And header Authorization = 'Bearer ' + token_carlos
    When method PUT
    Then status 400