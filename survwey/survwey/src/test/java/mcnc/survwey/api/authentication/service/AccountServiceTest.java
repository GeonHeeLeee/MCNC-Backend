package mcnc.survwey.api.authentication.service;

//@SpringBootTest
//@Transactional
class AccountServiceTest {

//    @Autowired
//    private AccountService accountService;
//
//    @Autowired
//    private UserRepository userRepository;
//
//    @Autowired
//    private PasswordEncoder passwordEncoder;
//
//    @Test
//    public void registerUserTest(){
//
//        AuthDTO authDTO = new AuthDTO();
//
//        authDTO.setEmail("asd@asdasd.com123");
//        authDTO.setPassword("qwer1234@@");
//        authDTO.setName("tester");
//        authDTO.setBirth(LocalDate.now());
//        authDTO.setGender(Gender.M);
//
//        accountService.registerUser(authDTO);
//        User user = userRepository.findById(authDTO.getEmail())
//                .orElseThrow(() -> new RuntimeException("User not found"));
//
//        System.out.println("user.getEmail() = " + user.getEmail());
//
//        assertThat(user.getEmail()).isEqualTo("asd@asdasd.com123");
//
//    }
//
//    @Test
//    public void modifyUserTest(){
//
//
//        ModifyDTO modifyDTO = new ModifyDTO();
//
//        modifyDTO.setEmail("asd@asdasd.com12");
//        modifyDTO.setName("modifyTest");
//        modifyDTO.setBirth(LocalDate.now());
//        modifyDTO.setGender(Gender.M);
//
//        accountService.modifyUser(modifyDTO);
//
//        User user = userRepository.findById(modifyDTO.getEmail())
//                .orElseThrow(() -> new RuntimeException("User가 존재하지 않습니다."));
//
//        assertThat(user.getName()).isEqualTo(modifyDTO.getName());
//        assertThat(user.getGender()).isEqualTo(modifyDTO.getGender());
//        assertThat(user.getBirth()).isEqualTo(modifyDTO.getBirth());
//
//    }
//
//    @Test
//    public void changePasswordTest(){
//
//        User user = new User();
//        user.setEmail("asd@asdasd.com12");
//        user.setPassword("qwer1234!@!@");
//        user.setName("tester");
//        user.setBirth(LocalDate.now());
//        user.setGender(Gender.M);
//        user.setRegisterDate(LocalDateTime.now());
//
//        userRepository.save(user);
//
//        ChangePasswordDTO changePasswordDTO = new ChangePasswordDTO();
//        changePasswordDTO.setEmail("asd@asdasd.com12");
//        changePasswordDTO.setPassword("qwerqwer1234@");
//
//        accountService.changePassword(changePasswordDTO);
//
//        User findUser = userRepository.findById(user.getEmail())
//                .orElseThrow(() ->
//                        new RuntimeException("User가 존재하지 않습니다."));
//
//        assertThat(passwordEncoder.matches(changePasswordDTO.getPassword(), findUser.getPassword())).isTrue();
//
//
//    }

}