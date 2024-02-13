package com.logicalis.apisolver.model;

import org.codehaus.jackson.annotate.JsonIgnore;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.ZoneId;

@Entity
public class SysUser implements Serializable {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(unique = true)
    private String integrationId;
    private String email;
    @JsonIgnore
    //@NotEmpty
    //@Size(max = 60)
    // @Column(nullable = false)
    private String password;
    private String code;

    //   @Column(nullable = false)
    private String name;

    private Date date_update_password;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "sys_user_rol", joinColumns = @JoinColumn(name = "id_user"), inverseJoinColumns = @JoinColumn(name = "id_rol"), uniqueConstraints = {
            @UniqueConstraint(columnNames = {"id_user", "id_rol"})})
    private List<Rol> roles;

    // @Column(nullable = false)
    private String lastName;

    @Column(name = "create_at")
    @Temporal(TemporalType.DATE)
    private Date createAt;

    //@Column(nullable = true)
    private boolean active;


    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "domain", referencedColumnName = "id")
    private Domain domain;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "company", referencedColumnName = "id")
    private Company company;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "location", referencedColumnName = "id")
    private Location location;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "department", referencedColumnName = "id")
    private Department department;

    @Column(columnDefinition = "boolean default false")
    private boolean vip;
    @Column(columnDefinition = "boolean default false")
    private boolean solver;
    private String employeeNumber;
    private String firstName;
    private String userName;
    private String solverPassword;
    private String mobilePhone;
    private String manager;
    @Column(columnDefinition = "boolean default false")
    private boolean locked;

    /*@ManyToOne
    @JoinColumn(name = "user_type", referencedColumnName = "id")
    private UserType user_type;*/

    @Column(columnDefinition = "int8 default 0")
    private Long userType;


    public Long getUserType() {
        return userType;
    }

    public void setUserType(String nameUserType) {
        this.userType = Long.valueOf(nameUserType);
    }

    /*
    public UserType getUserType() { return user_type;}

    public void setUserType(UserType userType) { this.user_type =userType ; }
    */

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @PrePersist
    public void prePersist() {
        createAt = new Date();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    private long CalculoTiempo(){
        if (date_update_password != null){
            LocalDateTime fechaEspecifica = date_update_password.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
            LocalDateTime fechaActual = LocalDateTime.now();
            System.out.println(fechaActual.toString());

            Duration duracion = Duration.between(fechaEspecifica, fechaActual);

            // La diferencia en días, horas, minutos y segundos
            long dias = duracion.toDays();
            //long horas = duracion.toHours() % 24;
            //long minutos = duracion.toMinutes() % 60;
            //long segundos = duracion.getSeconds() % 60;
            return dias;

        }else{
            // Cambiar la contraseña para tener update
            return 0;
        }
    }

    public String getPassword() { return password; }

    public void setPassword(String password) {
        this.password = password;
    }

    // Revisa la caducidad de las contraseñas anteriores solo de la compañia Scotiabank Chile
    public Boolean isPasswordExpired(long days_expiration){
        long dayUpdate = CalculoTiempo();
        if (dayUpdate >= days_expiration){ return true;
        }else{  return false;  }
    }

    public Date getDateUpdatePassword() {
        return date_update_password;
    }

    public void setdateUpdatePassword(Date date_update_password) {
        this.date_update_password = date_update_password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Date getCreateAt() {
        return createAt;
    }

    public void setCreateAt(Date createAt) {
        this.createAt = createAt;
    }

    public boolean getActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public List<Rol> getRoles() {
        return roles;
    }

    public void setRoles(List<Rol> roles) {
        this.roles = roles;
    }

    public String getIntegrationId() {
        return integrationId;
    }

    public void setIntegrationId(String integrationId) {
        this.integrationId = integrationId;
    }

    public Domain getDomain() {
        return domain;
    }

    public void setDomain(Domain domain) {
        this.domain = domain;
    }

    public Company getCompany() {
        return company;
    }

    public void setCompany(Company company) {
        this.company = company;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public Department getDepartment() {
        return department;
    }

    public void setDepartment(Department department) {
        this.department = department;
    }

    public String getEmployeeNumber() {
        return employeeNumber;
    }

    public void setEmployeeNumber(String employeeNumber) {
        this.employeeNumber = employeeNumber;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public boolean getVip() {
        return vip;
    }

    public void setVip(boolean vip) {
        this.vip = vip;
    }

    public boolean getSolver() {
        return solver;
    }

    public void setSolver(boolean solver) {
        this.solver = solver;
    }

    public String getSolverPassword() {
        return solverPassword;
    }

    public void setSolverPassword(String solverPassword) {
        this.solverPassword = solverPassword;
    }

    public boolean getLocked() {
        return locked;
    }

    public boolean isLocked() {
        return locked;
    }

    public void setLocked(boolean locked) {
        this.locked = locked;
    }

    public boolean isActive() {
        return active;
    }

    public boolean isVip() {
        return vip;
    }

    public boolean isSolver() {
        return solver;
    }

    public String getMobilePhone() {
        return mobilePhone;
    }

    public void setMobilePhone(String mobile_phone) {
        this.mobilePhone = mobile_phone;
    }

    public String getManager() {
        return manager;
    }

    public void setManager(String manager) {
        this.manager = manager;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    /**
     * es un atributo statico necesario
     */
    private static final long serialVersionUID = 1L;


}
