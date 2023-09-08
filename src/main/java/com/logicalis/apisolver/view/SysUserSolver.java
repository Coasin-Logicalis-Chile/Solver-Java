package com.logicalis.apisolver.view;

public class SysUserSolver  {
    private Long id;
    private String email;
    private boolean solver;
    private String password;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public boolean getSolver() {
        return solver;
    }

    public void setSolver(boolean solver) {
        this.solver = solver;
    }

    public boolean isSolver() {
        return solver;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
