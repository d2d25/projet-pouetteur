package com.pouetteur.notificationservice;

import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class NotificationServiceImplTest {
/*
    // Injecter les mocks dans l'implémentation du service à tester
    @InjectMocks
    private NotificationServiceImpl notificationService;

    // Créer des mocks pour les dépendances du service
    @Mock
    private NotificationRepository notificationRepository;

    @Mock
    private ModelMapper modelMapper;

    // Variables pour contenir les objets de test
    private NotificationDTO testNotificationDTO;
    private Notification testNotification;
    private Member testMember;

    // Méthode d'initialisation exécutée avant chaque test
    @BeforeEach
    public void setUp() {
        testMember = new Member("test_member_id");

        testNotification = new Notification("Test title", "Test body", "Test path", testMember.getId());
        testNotification.setId("1");

        testNotificationDTO = new NotificationDTO(testNotification);
    }

    // Test de la méthode createNotification du service
    @Test
    public void createNotificationTest() {
        // Configurer les comportements des mocks
        when(modelMapper.map(testNotificationDTO, Notification.class)).thenReturn(testNotification);
        when(notificationRepository.save(testNotification)).thenReturn(testNotification);
        when(modelMapper.map(testNotification, NotificationDTO.class)).thenReturn(testNotificationDTO);

        // Appeler la méthode à tester et récupérer le résultat
        NotificationDTO result = notificationService.createNotification(testNotificationDTO);

        // Vérifier que le résultat est correct
        assertEquals(testNotificationDTO, result);

        // Vérifier que les mocks ont été appelés comme prévu
        verify(modelMapper).map(testNotificationDTO, Notification.class);
        verify(notificationRepository).save(testNotification);
        verify(modelMapper).map(testNotification, NotificationDTO.class);
    }

    // Test de la méthode getNotification du service
    @Test
    public void getNotificationTest() throws NotificationNotFoundException {
        when(notificationRepository.findById("1")).thenReturn(Optional.of(testNotification));
        when(modelMapper.map(testNotification, NotificationDTO.class)).thenReturn(testNotificationDTO);

        NotificationDTO result = notificationService.getNotification("1");

        assertEquals(testNotificationDTO, result);
        verify(notificationRepository).findById("1");
        verify(modelMapper).map(testNotification, NotificationDTO.class);
    }

    // Test de la méthode updateNotification du service
    @Test
    public void updateNotificationTest() throws NotificationNotFoundException {
        Notification updatedNotification = new Notification("Updated title", "Updated body", "Updated path", testMember.getId());
        updatedNotification.setId("1");
        updatedNotification.setIs_read(true);
        NotificationDTO updatedNotificationDTO = new NotificationDTO(updatedNotification);

        when(notificationRepository.findById("1")).thenReturn(Optional.of(testNotification));
        when(notificationRepository.save(testNotification)).thenReturn(updatedNotification);
        when(modelMapper.map(updatedNotification, NotificationDTO.class)).thenReturn(updatedNotificationDTO);


        Object result = notificationService.updateNotification("1", updatedNotificationDTO);

        assertEquals(updatedNotificationDTO, result);
        verify(notificationRepository).findById("1");
        verify(notificationRepository).save(testNotification);
    }

    // Test de la méthode deleteNotification du service
    @Test
    public void deleteNotificationTest() {
        doNothing().when(notificationRepository).deleteById("1");

        notificationService.deleteNotification("1");

        verify(notificationRepository).deleteById("1");
    }

    // Test de la méthode getNotification lorsqu'une notification n'est pas trouvée
    @Test
    public void getNotificationNotFoundTest() {
        when(notificationRepository.findById("1")).thenReturn(Optional.empty());

        assertThrows(NotificationNotFoundException.class, () -> {
            notificationService.getNotification("1");
        });

        verify(notificationRepository).findById("1");
    }


    // Test de la méthode getNotificationsByMemberId du service
    @Test
    public void getNotificationsByMemberIdTest() {
        List<Notification> notifications = new ArrayList<>();
        notifications.add(testNotification);
        Notification secondNotification = new Notification("Another title", "Another body", "Another path", testMember.getId());
        secondNotification.setId("2");
        notifications.add(secondNotification);

        List<NotificationDTO> notificationDTOs = new ArrayList<>();
        notificationDTOs.add(testNotificationDTO);
        NotificationDTO secondNotificationDTO = new NotificationDTO(secondNotification);
        notificationDTOs.add(secondNotificationDTO);

        when(notificationRepository.findByMemberId(testMember.getId())).thenReturn(notifications);

        Type listType = new TypeToken<List<NotificationDTO>>() {}.getType();
        when(modelMapper.map(notifications, listType)).thenReturn(notificationDTOs);

        List<NotificationDTO> result = notificationService.getNotificationsByMemberId(testMember.getId());

        assertEquals(notificationDTOs, result);
        verify(notificationRepository).findByMemberId(testMember.getId());
        verify(modelMapper).map(notifications, listType);
    }

 */
}
