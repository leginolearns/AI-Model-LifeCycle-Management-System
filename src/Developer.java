public class Developer extends User {

    private String programmingLanguage;

    public Developer(int userId, String name, String email,
                     String programmingLanguage) {
        super(userId, name, email);
        this.programmingLanguage = programmingLanguage;
    }

    public String getProgrammingLanguage() {
        return programmingLanguage;
    }

    public void setProgrammingLanguage(String programmingLanguage) {
        this.programmingLanguage = programmingLanguage;
    }

    @Override
    public void displayRole() {
        System.out.println("Role    : Developer");
        System.out.println("Language: " + programmingLanguage);
    }
}
