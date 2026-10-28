public class DataScientist extends User {

    private String specialization;

    public DataScientist(int userId, String name, String email,
                         String specialization) {
        super(userId, name, email);
        this.specialization = specialization;
    }

    public String getSpecialization() {
        return specialization;
    }

    public void setSpecialization(String specialization) {
        this.specialization = specialization;
    }

    @Override
    public void displayRole() {
        System.out.println("Role          : Data Scientist");
        System.out.println("Specialization: " + specialization);
    }
}
