package com.humanitaire.backend.config;

import com.humanitaire.backend.entity.*;
import com.humanitaire.backend.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

@Component
@RequiredArgsConstructor
public class DataSeeder implements CommandLineRunner {

    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final MissionRepository missionRepository;
    private final VolunteerRepository volunteerRepository;
    private final BeneficiaryRepository beneficiaryRepository;
    private final DonationRepository donationRepository;
    private final ConvoyRepository convoyRepository;
    private final EventRepository eventRepository;
    private final NotificationRepository notificationRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        if (roleRepository.count() == 0) {
            seedRoles();
            seedUsers();
            seedMissions();
            seedVolunteers();
            seedBeneficiaries();
            seedDonations();
            seedConvoys();
            seedEvents();
            seedNotifications();
        }
    }

    private void seedRoles() {
        for (Role.RoleName name : Role.RoleName.values()) {
            roleRepository.save(Role.builder().name(name).build());
        }
    }

    private void seedUsers() {
        Role adminRole = roleRepository.findByName(Role.RoleName.ROLE_SUPER_ADMIN).orElseThrow();
        Role managerRole = roleRepository.findByName(Role.RoleName.ROLE_MISSION_MANAGER).orElseThrow();
        Role volunteerRole = roleRepository.findByName(Role.RoleName.ROLE_VOLUNTEER).orElseThrow();
        Role donorRole = roleRepository.findByName(Role.RoleName.ROLE_DONOR).orElseThrow();

        userRepository.save(User.builder().firstName("Admin").lastName("Principal").email("admin@humanitaire.ma").password(passwordEncoder.encode("admin123")).roles(Set.of(adminRole)).enabled(true).city("Rabat").region("Rabat-Salé-Kénitra").build());
        userRepository.save(User.builder().firstName("Mohammed").lastName("Alami").email("manager@humanitaire.ma").password(passwordEncoder.encode("manager123")).roles(Set.of(managerRole)).enabled(true).city("Casablanca").region("Casablanca-Settat").build());
        userRepository.save(User.builder().firstName("Fatima").lastName("Benali").email("volunteer@humanitaire.ma").password(passwordEncoder.encode("volunteer123")).roles(Set.of(volunteerRole)).enabled(true).city("Marrakech").region("Marrakech-Safi").build());
        userRepository.save(User.builder().firstName("Ahmed").lastName("Tazi").email("donor@humanitaire.ma").password(passwordEncoder.encode("donor123")).roles(Set.of(donorRole)).enabled(true).city("Fès").region("Fès-Meknès").build());
    }

    private void seedMissions() {
        String[][] missions = {
            {"Distribution alimentaire - Al Haouz", "Distribution de paniers alimentaires aux familles touchées par le séisme dans la région d'Al Haouz", "Marrakech", "Marrakech-Safi", "31.6295", "-7.9811", "ACTIVE", "CRITICAL"},
            {"Reconstruction d'écoles - Taroudant", "Reconstruction et réhabilitation des écoles endommagées dans la province de Taroudant", "Taroudant", "Souss-Massa", "30.4727", "-8.8748", "ACTIVE", "HIGH"},
            {"Aide médicale - Ouarzazate", "Caravane médicale pour les populations rurales de la région de Ouarzazate", "Ouarzazate", "Drâa-Tafilalet", "30.9189", "-6.8936", "PLANNED", "HIGH"},
            {"Soutien scolaire - Casablanca", "Programme de soutien scolaire pour les enfants des quartiers défavorisés", "Casablanca", "Casablanca-Settat", "33.5731", "-7.5898", "ACTIVE", "MEDIUM"},
            {"Distribution de vêtements - Fès", "Collecte et distribution de vêtements d'hiver pour les sans-abris", "Fès", "Fès-Meknès", "34.0181", "-5.0078", "COMPLETED", "MEDIUM"},
            {"Construction de puits - Errachidia", "Forage et construction de puits dans les zones rurales arides", "Errachidia", "Drâa-Tafilalet", "31.9314", "-4.4267", "PLANNED", "HIGH"},
            {"Alphabétisation - Tanger", "Programme d'alphabétisation pour les femmes rurales de la région de Tanger", "Tanger", "Tanger-Tétouan-Al Hoceïma", "35.7595", "-5.8340", "ACTIVE", "MEDIUM"},
            {"Aide aux réfugiés - Nador", "Assistance humanitaire aux réfugiés et migrants à Nador", "Nador", "Oriental", "35.1688", "-2.9335", "ACTIVE", "CRITICAL"},
            {"Micro-crédit - Agadir", "Programme de micro-crédit pour les femmes entrepreneures", "Agadir", "Souss-Massa", "30.4278", "-9.5981", "PLANNED", "LOW"},
            {"Vaccination - Béni Mellal", "Campagne de vaccination dans les zones rurales", "Béni Mellal", "Béni Mellal-Khénifra", "32.3372", "-6.3498", "COMPLETED", "HIGH"},
            {"Logements d'urgence - Chichaoua", "Construction de logements temporaires pour les sinistrés du séisme", "Chichaoua", "Marrakech-Safi", "31.5447", "-8.7609", "ACTIVE", "CRITICAL"},
            {"Formation professionnelle - Meknès", "Formation en métiers de l'artisanat pour les jeunes chômeurs", "Meknès", "Fès-Meknès", "33.8935", "-5.5547", "ACTIVE", "MEDIUM"},
            {"Eau potable - Zagora", "Installation de systèmes de purification d'eau", "Zagora", "Drâa-Tafilalet", "30.3320", "-5.8380", "PLANNED", "HIGH"},
            {"Protection de l'enfance - Salé", "Programme de protection et d'accompagnement des enfants en situation difficile", "Salé", "Rabat-Salé-Kénitra", "34.0531", "-6.7986", "ACTIVE", "HIGH"},
            {"Agriculture solidaire - Khénifra", "Soutien aux petits agriculteurs avec semences et matériel", "Khénifra", "Béni Mellal-Khénifra", "32.9389", "-5.6713", "PLANNED", "MEDIUM"},
            {"Aide psychologique - Al Haouz", "Soutien psychologique aux survivants du séisme", "Al Haouz", "Marrakech-Safi", "31.3600", "-7.9000", "ACTIVE", "CRITICAL"},
            {"Énergie solaire - Tinghir", "Installation de panneaux solaires dans les villages isolés", "Tinghir", "Drâa-Tafilalet", "31.5146", "-5.5302", "PLANNED", "MEDIUM"},
            {"Santé maternelle - Tata", "Amélioration des services de santé maternelle", "Tata", "Souss-Massa", "29.7483", "-7.9719", "ACTIVE", "HIGH"},
            {"Reforestation - Ifrane", "Campagne de plantation d'arbres dans la région d'Ifrane", "Ifrane", "Fès-Meknès", "33.5228", "-5.1108", "COMPLETED", "LOW"},
            {"Aide alimentaire - Laâyoune", "Distribution de denrées alimentaires aux familles démunies", "Laâyoune", "Laâyoune-Sakia El Hamra", "27.1536", "-13.2034", "ACTIVE", "MEDIUM"},
            {"Transport scolaire - Azilal", "Mise en place de transport scolaire pour les élèves ruraux", "Azilal", "Béni Mellal-Khénifra", "31.9608", "-6.5732", "PLANNED", "MEDIUM"},
            {"Centres d'accueil - Oujda", "Création de centres d'accueil pour les personnes vulnérables", "Oujda", "Oriental", "34.6814", "-1.9086", "ACTIVE", "HIGH"},
            {"Aide aux handicapés - Rabat", "Programme d'insertion des personnes en situation de handicap", "Rabat", "Rabat-Salé-Kénitra", "34.0209", "-6.8416", "ACTIVE", "MEDIUM"},
            {"Lutte contre l'abandon scolaire - Tétouan", "Programme de prévention de l'abandon scolaire", "Tétouan", "Tanger-Tétouan-Al Hoceïma", "35.5785", "-5.3684", "PLANNED", "HIGH"},
            {"Solidarité Ramadan - Nationwide", "Distribution de paniers Ramadan aux familles nécessiteuses", "Rabat", "Rabat-Salé-Kénitra", "34.0209", "-6.8416", "COMPLETED", "MEDIUM"},
            {"Caravane dentaire - Taza", "Soins dentaires gratuits pour les populations rurales", "Taza", "Fès-Meknès", "34.2100", "-4.0100", "PLANNED", "LOW"},
            {"Hébergement hivernal - Midelt", "Hébergement d'urgence pendant la vague de froid", "Midelt", "Drâa-Tafilalet", "32.6802", "-4.7400", "COMPLETED", "HIGH"},
            {"Soutien aux orphelins - Kénitra", "Parrainage et accompagnement des orphelins", "Kénitra", "Rabat-Salé-Kénitra", "34.2610", "-6.5802", "ACTIVE", "MEDIUM"},
            {"Accès internet - Dakhla", "Connectivité internet pour les écoles rurales", "Dakhla", "Dakhla-Oued Ed-Dahab", "23.7148", "-15.9370", "PLANNED", "LOW"},
            {"Formation premiers secours - Settat", "Formation en secourisme pour les bénévoles locaux", "Settat", "Casablanca-Settat", "33.0016", "-7.6166", "ACTIVE", "MEDIUM"}
        };

        Random random = new Random(42);
        for (String[] m : missions) {
            Mission mission = Mission.builder()
                .title(m[0])
                .description(m[1])
                .city(m[2])
                .region(m[3])
                .latitude(Double.parseDouble(m[4]))
                .longitude(Double.parseDouble(m[5]))
                .status(Mission.MissionStatus.valueOf(m[6]))
                .priority(Mission.MissionPriority.valueOf(m[7]))
                .startDate(LocalDate.now().minusDays(random.nextInt(90)))
                .endDate(LocalDate.now().plusDays(random.nextInt(180)))
                .budget(50000.0 + random.nextInt(450000))
                .priorityScore(random.nextInt(100))
                .build();
            missionRepository.save(mission);
        }
    }

    private void seedVolunteers() {
        String[][] volunteers = {
            {"Youssef", "El Amrani", "youssef.amrani@email.com", "0661234567", "Casablanca", "Casablanca-Settat", "Logistique,Premiers secours"},
            {"Amina", "Benkirane", "amina.benkirane@email.com", "0662345678", "Rabat", "Rabat-Salé-Kénitra", "Enseignement,Communication"},
            {"Karim", "Idrissi", "karim.idrissi@email.com", "0663456789", "Marrakech", "Marrakech-Safi", "Médecine,Urgences"},
            {"Salma", "Tazi", "salma.tazi@email.com", "0664567890", "Fès", "Fès-Meknès", "Psychologie,Accompagnement"},
            {"Omar", "Hassani", "omar.hassani@email.com", "0665678901", "Tanger", "Tanger-Tétouan-Al Hoceïma", "Construction,Maçonnerie"},
            {"Khadija", "Mouline", "khadija.mouline@email.com", "0666789012", "Agadir", "Souss-Massa", "Cuisine,Nutrition"},
            {"Rachid", "Bennani", "rachid.bennani@email.com", "0667890123", "Oujda", "Oriental", "Transport,Conduite"},
            {"Fatima-Zahra", "El Fassi", "fz.elfassi@email.com", "0668901234", "Meknès", "Fès-Meknès", "Informatique,Gestion"},
            {"Hamza", "Chraibi", "hamza.chraibi@email.com", "0669012345", "Tétouan", "Tanger-Tétouan-Al Hoceïma", "Agriculture,Environnement"},
            {"Nadia", "Berrada", "nadia.berrada@email.com", "0670123456", "Casablanca", "Casablanca-Settat", "Finance,Comptabilité"},
            {"Mehdi", "Alaoui", "mehdi.alaoui@email.com", "0671234567", "Rabat", "Rabat-Salé-Kénitra", "Droit,Juridique"},
            {"Leila", "Sqalli", "leila.sqalli@email.com", "0672345678", "Marrakech", "Marrakech-Safi", "Infirmerie,Soins"},
            {"Amine", "Kadiri", "amine.kadiri@email.com", "0673456789", "Fès", "Fès-Meknès", "Électricité,Plomberie"},
            {"Sara", "Filali", "sara.filali@email.com", "0674567890", "Tanger", "Tanger-Tétouan-Al Hoceïma", "Traduction,Langues"},
            {"Zakaria", "Ouazzani", "zakaria.ouazzani@email.com", "0675678901", "Agadir", "Souss-Massa", "Mécanique,Technique"},
            {"Houda", "Benjelloun", "houda.benjelloun@email.com", "0676789012", "Casablanca", "Casablanca-Settat", "Marketing,Design"},
            {"Ilyas", "Tahiri", "ilyas.tahiri@email.com", "0677890123", "Rabat", "Rabat-Salé-Kénitra", "Médecine,Chirurgie"},
            {"Meryem", "Zniber", "meryem.zniber@email.com", "0678901234", "Marrakech", "Marrakech-Safi", "Enseignement,Formation"},
            {"Adil", "Lamrani", "adil.lamrani@email.com", "0679012345", "Oujda", "Oriental", "Logistique,Organisation"},
            {"Sanaa", "Guessous", "sanaa.guessous@email.com", "0680123456", "Meknès", "Fès-Meknès", "Social,Psychologie"},
            {"Younes", "Kettani", "younes.kettani@email.com", "0681234567", "Casablanca", "Casablanca-Settat", "Construction,Architecture"},
            {"Rim", "Belhaj", "rim.belhaj@email.com", "0682345678", "Rabat", "Rabat-Salé-Kénitra", "Communication,Journalisme"},
            {"Othmane", "El Ghazi", "othmane.elghazi@email.com", "0683456789", "Marrakech", "Marrakech-Safi", "Informatique,Développement"},
            {"Imane", "Sefrioui", "imane.sefrioui@email.com", "0684567890", "Fès", "Fès-Meknès", "Pharmacie,Santé"},
            {"Khalid", "Benmoussa", "khalid.benmoussa@email.com", "0685678901", "Tanger", "Tanger-Tétouan-Al Hoceïma", "Pêche,Maritime"},
            {"Aicha", "El Ouafi", "aicha.elouafi@email.com", "0686789012", "Agadir", "Souss-Massa", "Artisanat,Couture"},
            {"Badr", "Fassi Fihri", "badr.fassifihri@email.com", "0687890123", "Casablanca", "Casablanca-Settat", "Sport,Animation"},
            {"Zineb", "Lahlou", "zineb.lahlou@email.com", "0688901234", "Rabat", "Rabat-Salé-Kénitra", "Nutrition,Diététique"},
            {"Reda", "Cherkaoui", "reda.cherkaoui@email.com", "0689012345", "Marrakech", "Marrakech-Safi", "Sécurité,Protection"},
            {"Hajar", "Mekouar", "hajar.mekouar@email.com", "0690123456", "Fès", "Fès-Meknès", "Administration,Gestion"},
            {"Soufiane", "Andaloussi", "soufiane.anda@email.com", "0691234567", "Tanger", "Tanger-Tétouan-Al Hoceïma", "Photographie,Vidéo"},
            {"Ghita", "Boucetta", "ghita.boucetta@email.com", "0692345678", "Agadir", "Souss-Massa", "Éducation,Maternelle"},
            {"Yassine", "El Mansouri", "yassine.mansouri@email.com", "0693456789", "Oujda", "Oriental", "Électronique,Réparation"},
            {"Loubna", "Saidi", "loubna.saidi@email.com", "0694567890", "Meknès", "Fès-Meknès", "Sage-femme,Obstétrique"},
            {"Ayoub", "Regragui", "ayoub.regragui@email.com", "0695678901", "Casablanca", "Casablanca-Settat", "Cuisine,Boulangerie"},
            {"Wiam", "Lazrak", "wiam.lazrak@email.com", "0696789012", "Rabat", "Rabat-Salé-Kénitra", "Arts,Théâtre"},
            {"Sami", "Bouabid", "sami.bouabid@email.com", "0697890123", "Marrakech", "Marrakech-Safi", "Menuiserie,Bois"},
            {"Kenza", "Slimani", "kenza.slimani@email.com", "0698901234", "Tanger", "Tanger-Tétouan-Al Hoceïma", "Coiffure,Esthétique"},
            {"Taha", "El Ouarzazi", "taha.ouarzazi@email.com", "0699012345", "Errachidia", "Drâa-Tafilalet", "Agriculture,Irrigation"},
            {"Mariam", "Bensouda", "mariam.bensouda@email.com", "0600123456", "Kénitra", "Rabat-Salé-Kénitra", "Musique,Culture"},
            {"Ayman", "Naciri", "ayman.naciri@email.com", "0601234567", "Settat", "Casablanca-Settat", "Maçonnerie,BTP"},
            {"Salwa", "El Kabbaj", "salwa.elkabbaj@email.com", "0602345678", "Béni Mellal", "Béni Mellal-Khénifra", "Infirmerie,Soins"},
            {"Bilal", "Laroui", "bilal.laroui@email.com", "0603456789", "Nador", "Oriental", "Pêche,Navigation"},
            {"Hafsa", "Doukkali", "hafsa.doukkali@email.com", "0604567890", "Tétouan", "Tanger-Tétouan-Al Hoceïma", "Jardinage,Horticulture"},
            {"Mouad", "Ameziane", "mouad.ameziane@email.com", "0605678901", "Laâyoune", "Laâyoune-Sakia El Hamra", "Mécanique,Soudure"},
            {"Nisrine", "El Harti", "nisrine.elharti@email.com", "0606789012", "Dakhla", "Dakhla-Oued Ed-Dahab", "Tourisme,Guide"},
            {"Walid", "Benchekroun", "walid.benchekroun@email.com", "0607890123", "Casablanca", "Casablanca-Settat", "Finance,Audit"},
            {"Kawtar", "Mernissi", "kawtar.mernissi@email.com", "0608901234", "Rabat", "Rabat-Salé-Kénitra", "Recherche,Science"},
            {"Driss", "Belkadi", "driss.belkadi@email.com", "0609012345", "Marrakech", "Marrakech-Safi", "Conduite,Transport"},
            {"Asmaa", "El Ghali", "asmaa.elghali@email.com", "0610123456", "Fès", "Fès-Meknès", "Kinésithérapie,Rééducation"}
        };

        Random random = new Random(42);
        for (String[] v : volunteers) {
            Volunteer volunteer = Volunteer.builder()
                .firstName(v[0])
                .lastName(v[1])
                .email(v[2])
                .phone(v[3])
                .city(v[4])
                .region(v[5])
                .skills(v[6])
                .available(random.nextBoolean() || random.nextBoolean())
                .address(v[4] + ", Maroc")
                .build();
            volunteerRepository.save(volunteer);
        }
    }

    private void seedBeneficiaries() {
        String[][] names = {
            {"Famille Amrani", "Famille Benkirane", "Famille El Ouafi", "Famille Hassani", "Famille Tazi"},
            {"Famille Mouline", "Famille Chraibi", "Famille Kadiri", "Famille Berrada", "Famille Filali"},
            {"Famille Alaoui", "Famille Sqalli", "Famille Ouazzani", "Famille Benjelloun", "Famille Tahiri"},
            {"Famille Zniber", "Famille Lamrani", "Famille Guessous", "Famille Kettani", "Famille Belhaj"}
        };
        String[] cities = {"Casablanca", "Rabat", "Marrakech", "Fès", "Tanger", "Agadir", "Oujda", "Meknès", "Tétouan", "Nador"};
        String[] regions = {"Casablanca-Settat", "Rabat-Salé-Kénitra", "Marrakech-Safi", "Fès-Meknès", "Tanger-Tétouan-Al Hoceïma", "Souss-Massa", "Oriental", "Fès-Meknès", "Tanger-Tétouan-Al Hoceïma", "Oriental"};

        List<Mission> missions = missionRepository.findAll();
        Random random = new Random(42);

        int count = 0;
        for (String[] group : names) {
            for (String name : group) {
                for (int i = 1; i <= 5; i++) {
                    int cityIdx = random.nextInt(cities.length);
                    Beneficiary ben = Beneficiary.builder()
                        .fullName(name + " " + i)
                        .familySize(2 + random.nextInt(8))
                        .emergencyLevel(Beneficiary.EmergencyLevel.values()[random.nextInt(4)])
                        .city(cities[cityIdx])
                        .region(regions[cityIdx])
                        .address(cities[cityIdx] + ", Quartier " + (random.nextInt(20) + 1))
                        .phone("06" + String.format("%08d", random.nextInt(100000000)))
                        .needs("Aide alimentaire, Vêtements, Médicaments")
                        .mission(!missions.isEmpty() ? missions.get(random.nextInt(missions.size())) : null)
                        .build();
                    beneficiaryRepository.save(ben);
                    count++;
                    if (count >= 100) return;
                }
            }
        }
    }

    private void seedDonations() {
        String[] donors = {"Ahmed Bennani", "Khalid Fassi", "Sara Tazi", "Mohammed Alami", "Nadia Berrada",
            "Youssef Idrissi", "Fatima Hassani", "Omar El Amrani", "Salma Kadiri", "Rachid Mouline"};
        String[] emails = {"ahmed.b@email.com", "khalid.f@email.com", "sara.t@email.com", "mohammed.a@email.com",
            "nadia.b@email.com", "youssef.i@email.com", "fatima.h@email.com", "omar.a@email.com",
            "salma.k@email.com", "rachid.m@email.com"};

        List<Mission> missions = missionRepository.findAll();
        Random random = new Random(42);

        for (int i = 0; i < 100; i++) {
            int donorIdx = random.nextInt(donors.length);
            Donation donation = Donation.builder()
                .donorName(donors[donorIdx])
                .donorEmail(emails[donorIdx])
                .amount(100.0 + random.nextInt(9900))
                .currency("MAD")
                .paymentMethod(Donation.PaymentMethod.values()[random.nextInt(5)])
                .status(Donation.DonationStatus.COMPLETED)
                .transactionId(UUID.randomUUID().toString().substring(0, 12).toUpperCase())
                .description("Don pour mission humanitaire")
                .mission(!missions.isEmpty() ? missions.get(random.nextInt(missions.size())) : null)
                .build();
            donationRepository.save(donation);
        }
    }

    private void seedConvoys() {
        String[][] convoys = {
            {"Convoi Alimentaire Casa-Marrakech", "Casablanca", "Marrakech", "33.5731", "-7.5898", "31.6295", "-7.9811", "IN_TRANSIT", "Denrées alimentaires, Eau"},
            {"Convoi Médical Rabat-Al Haouz", "Rabat", "Al Haouz", "34.0209", "-6.8416", "31.3600", "-7.9000", "IN_TRANSIT", "Médicaments, Équipement médical"},
            {"Convoi Matériaux Tanger-Chichaoua", "Tanger", "Chichaoua", "35.7595", "-5.8340", "31.5447", "-8.7609", "PENDING", "Matériaux de construction"},
            {"Convoi Vêtements Fès-Errachidia", "Fès", "Errachidia", "34.0181", "-5.0078", "31.9314", "-4.4267", "DELIVERED", "Vêtements, Couvertures"},
            {"Convoi Scolaire Agadir-Taroudant", "Agadir", "Taroudant", "30.4278", "-9.5981", "30.4727", "-8.8748", "IN_TRANSIT", "Fournitures scolaires"},
            {"Convoi Eau Meknès-Ifrane", "Meknès", "Ifrane", "33.8935", "-5.5547", "33.5228", "-5.1108", "DELIVERED", "Eau potable, Citernes"},
            {"Convoi Agricole Béni Mellal-Azilal", "Béni Mellal", "Azilal", "32.3372", "-6.3498", "31.9608", "-6.5732", "PENDING", "Semences, Outils agricoles"},
            {"Convoi Urgence Oujda-Nador", "Oujda", "Nador", "34.6814", "-1.9086", "35.1688", "-2.9335", "IN_TRANSIT", "Aide d'urgence"},
            {"Convoi Hivernal Casa-Midelt", "Casablanca", "Midelt", "33.5731", "-7.5898", "32.6802", "-4.7400", "DELAYED", "Chauffages, Couvertures"},
            {"Convoi Solaire Rabat-Tinghir", "Rabat", "Tinghir", "34.0209", "-6.8416", "31.5146", "-5.5302", "PENDING", "Panneaux solaires"},
            {"Convoi Alimentaire Tanger-Tétouan", "Tanger", "Tétouan", "35.7595", "-5.8340", "35.5785", "-5.3684", "DELIVERED", "Denrées alimentaires"},
            {"Convoi Médical Marrakech-Zagora", "Marrakech", "Zagora", "31.6295", "-7.9811", "30.3320", "-5.8380", "IN_TRANSIT", "Équipement médical"},
            {"Convoi Reconstruction Agadir-Tata", "Agadir", "Tata", "30.4278", "-9.5981", "29.7483", "-7.9719", "PENDING", "Matériaux, Outils"},
            {"Convoi Éducatif Rabat-Kénitra", "Rabat", "Kénitra", "34.0209", "-6.8416", "34.2610", "-6.5802", "DELIVERED", "Livres, Ordinateurs"},
            {"Convoi Alimentaire Casa-Settat", "Casablanca", "Settat", "33.5731", "-7.5898", "33.0016", "-7.6166", "IN_TRANSIT", "Nourriture, Eau"},
            {"Convoi Sanitaire Fès-Taza", "Fès", "Taza", "34.0181", "-5.0078", "34.2100", "-4.0100", "PENDING", "Matériel sanitaire"},
            {"Convoi Sud Agadir-Laâyoune", "Agadir", "Laâyoune", "30.4278", "-9.5981", "27.1536", "-13.2034", "IN_TRANSIT", "Aide diverse"},
            {"Convoi Extrême Sud Casa-Dakhla", "Casablanca", "Dakhla", "33.5731", "-7.5898", "23.7148", "-15.9370", "DELAYED", "Équipements"},
            {"Convoi Textile Fès-Meknès", "Fès", "Meknès", "34.0181", "-5.0078", "33.8935", "-5.5547", "DELIVERED", "Vêtements, Textiles"},
            {"Convoi Urgence Tanger-Al Hoceïma", "Tanger", "Al Hoceïma", "35.7595", "-5.8340", "35.2517", "-3.9372", "PENDING", "Aide d'urgence"}
        };

        Random random = new Random(42);
        List<Mission> missions = missionRepository.findAll();

        for (String[] c : convoys) {
            double depLat = Double.parseDouble(c[4]);
            double depLng = Double.parseDouble(c[5]);
            double destLat = Double.parseDouble(c[6]);
            double destLng = Double.parseDouble(c[7]);
            double currentLat = depLat + (destLat - depLat) * random.nextDouble();
            double currentLng = depLng + (destLng - depLng) * random.nextDouble();

            Convoy convoy = Convoy.builder()
                .name(c[0])
                .departureCity(c[1])
                .destinationCity(c[2])
                .departureLatitude(depLat)
                .departureLongitude(depLng)
                .destinationLatitude(destLat)
                .destinationLongitude(destLng)
                .currentLatitude(currentLat)
                .currentLongitude(currentLng)
                .status(Convoy.ConvoyStatus.valueOf(c[3]))
                .cargo(c[8])
                .departureTime(LocalDateTime.now().minusDays(random.nextInt(10)))
                .estimatedArrival(LocalDateTime.now().plusDays(random.nextInt(5)))
                .mission(!missions.isEmpty() ? missions.get(random.nextInt(missions.size())) : null)
                .build();
            convoyRepository.save(convoy);
        }
    }

    private void seedEvents() {
        String[][] events = {
            {"Gala de Charité Annuel", "Soirée de collecte de fonds pour les missions humanitaires", "Casablanca", "Casablanca-Settat"},
            {"Marathon Solidaire de Marrakech", "Course caritative au profit des sinistrés du séisme", "Marrakech", "Marrakech-Safi"},
            {"Forum des Bénévoles", "Rencontre et formation des nouveaux bénévoles", "Rabat", "Rabat-Salé-Kénitra"},
            {"Exposition Artisanale Solidaire", "Vente d'artisanat au profit des familles démunies", "Fès", "Fès-Meknès"},
            {"Concert Caritatif", "Concert de musique andalouse pour la solidarité", "Tanger", "Tanger-Tétouan-Al Hoceïma"},
            {"Journée Portes Ouvertes", "Visite des centres d'aide et rencontre des bénéficiaires", "Agadir", "Souss-Massa"},
            {"Conférence Humanitaire", "Débat sur les enjeux humanitaires au Maroc", "Rabat", "Rabat-Salé-Kénitra"},
            {"Tournoi Sportif Solidaire", "Compétition sportive pour la bonne cause", "Meknès", "Fès-Meknès"},
            {"Bazar Caritatif", "Marché solidaire avec produits locaux", "Casablanca", "Casablanca-Settat"},
            {"Formation Secourisme", "Initiation aux gestes de premiers secours", "Oujda", "Oriental"},
            {"Iftar Collectif", "Repas de rupture du jeûne pour les personnes sans-abri", "Casablanca", "Casablanca-Settat"},
            {"Journée de l'Environnement", "Nettoyage des plages et sensibilisation écologique", "Tanger", "Tanger-Tétouan-Al Hoceïma"},
            {"Atelier Couture Solidaire", "Formation en couture pour les femmes", "Marrakech", "Marrakech-Safi"},
            {"Course Cycliste Caritative", "Randonnée à vélo au profit des orphelins", "Rabat", "Rabat-Salé-Kénitra"},
            {"Salon du Livre Solidaire", "Collecte et distribution de livres scolaires", "Fès", "Fès-Meknès"}
        };

        Random random = new Random(42);
        Event.EventStatus[] statuses = Event.EventStatus.values();

        for (String[] e : events) {
            Event event = Event.builder()
                .title(e[0])
                .description(e[1])
                .city(e[2])
                .region(e[3])
                .location(e[2] + ", Maroc")
                .eventDate(LocalDate.now().plusDays(random.nextInt(180) - 30))
                .organizer("Association Humanitaire Maroc")
                .maxParticipants(50 + random.nextInt(450))
                .currentParticipants(random.nextInt(200))
                .status(statuses[random.nextInt(statuses.length)])
                .build();
            eventRepository.save(event);
        }
    }

    private void seedNotifications() {
        User admin = userRepository.findByEmail("admin@humanitaire.ma").orElse(null);
        if (admin != null) {
            notificationRepository.save(Notification.builder().title("Bienvenue").message("Bienvenue sur la plateforme humanitaire du Maroc").type(Notification.NotificationType.INFO).user(admin).build());
            notificationRepository.save(Notification.builder().title("Nouvelle mission").message("Une nouvelle mission critique a été créée à Al Haouz").type(Notification.NotificationType.MISSION).user(admin).build());
            notificationRepository.save(Notification.builder().title("Don reçu").message("Un don de 5000 MAD a été reçu").type(Notification.NotificationType.DONATION).user(admin).build());
            notificationRepository.save(Notification.builder().title("Événement à venir").message("Le Gala de Charité Annuel aura lieu dans 7 jours").type(Notification.NotificationType.EVENT).user(admin).build());
        }
    }
}
