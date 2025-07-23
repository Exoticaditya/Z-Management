package com.zplus.adminpanel.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Dashboard Statistics Data Transfer Object")
public class DashboardStatsDTO {

    @Schema(description = "Total number of users in the system")
    @JsonProperty("totalUsers")
    private Long totalUsers;

    @Schema(description = "Number of active projects")
    @JsonProperty("activeProjects")
    private Long activeProjects;

    @Schema(description = "Number of pending approvals")
    @JsonProperty("pendingApprovals")
    private Long pendingApprovals;

    @Schema(description = "Number of online users")
    @JsonProperty("onlineUsers")
    private Long onlineUsers;

    @Schema(description = "System performance percentage")
    @JsonProperty("systemPerformance")
    private Double systemPerformance;

    @Schema(description = "Total completed projects")
    @JsonProperty("completedProjects")
    private Long completedProjects;

    @Schema(description = "Total revenue generated")
    @JsonProperty("totalRevenue")
    private Double totalRevenue;

    @Schema(description = "Number of employees")
    @JsonProperty("totalEmployees")
    private Long totalEmployees;

    @Schema(description = "Number of clients")
    @JsonProperty("totalClients")
    private Long totalClients;

    @Schema(description = "Number of admins")
    @JsonProperty("totalAdmins")
    private Long totalAdmins;

    // Default constructor
    public DashboardStatsDTO() {}

    // Constructor with all fields
    public DashboardStatsDTO(Long totalUsers, Long activeProjects, Long pendingApprovals, 
                           Long onlineUsers, Double systemPerformance, Long completedProjects,
                           Double totalRevenue, Long totalEmployees, Long totalClients, Long totalAdmins) {
        this.totalUsers = totalUsers;
        this.activeProjects = activeProjects;
        this.pendingApprovals = pendingApprovals;
        this.onlineUsers = onlineUsers;
        this.systemPerformance = systemPerformance;
        this.completedProjects = completedProjects;
        this.totalRevenue = totalRevenue;
        this.totalEmployees = totalEmployees;
        this.totalClients = totalClients;
        this.totalAdmins = totalAdmins;
    }

    // Getters and Setters
    public Long getTotalUsers() {
        return totalUsers != null ? totalUsers : 0L;
    }

    public void setTotalUsers(Long totalUsers) {
        this.totalUsers = totalUsers;
    }

    public Long getActiveProjects() {
        return activeProjects != null ? activeProjects : 0L;
    }

    public void setActiveProjects(Long activeProjects) {
        this.activeProjects = activeProjects;
    }

    public Long getPendingApprovals() {
        return pendingApprovals != null ? pendingApprovals : 0L;
    }

    public void setPendingApprovals(Long pendingApprovals) {
        this.pendingApprovals = pendingApprovals;
    }

    public Long getOnlineUsers() {
        return onlineUsers != null ? onlineUsers : 0L;
    }

    public void setOnlineUsers(Long onlineUsers) {
        this.onlineUsers = onlineUsers;
    }

    public Double getSystemPerformance() {
        return systemPerformance != null ? systemPerformance : 98.5;
    }

    public void setSystemPerformance(Double systemPerformance) {
        this.systemPerformance = systemPerformance;
    }

    public Long getCompletedProjects() {
        return completedProjects != null ? completedProjects : 0L;
    }

    public void setCompletedProjects(Long completedProjects) {
        this.completedProjects = completedProjects;
    }

    public Double getTotalRevenue() {
        return totalRevenue != null ? totalRevenue : 0.0;
    }

    public void setTotalRevenue(Double totalRevenue) {
        this.totalRevenue = totalRevenue;
    }

    public Long getTotalEmployees() {
        return totalEmployees != null ? totalEmployees : 0L;
    }

    public void setTotalEmployees(Long totalEmployees) {
        this.totalEmployees = totalEmployees;
    }

    public Long getTotalClients() {
        return totalClients != null ? totalClients : 0L;
    }

    public void setTotalClients(Long totalClients) {
        this.totalClients = totalClients;
    }

    public Long getTotalAdmins() {
        return totalAdmins != null ? totalAdmins : 0L;
    }

    public void setTotalAdmins(Long totalAdmins) {
        this.totalAdmins = totalAdmins;
    }

    @Override
    public String toString() {
        return "DashboardStatsDTO{" +
                "totalUsers=" + totalUsers +
                ", activeProjects=" + activeProjects +
                ", pendingApprovals=" + pendingApprovals +
                ", onlineUsers=" + onlineUsers +
                ", systemPerformance=" + systemPerformance +
                ", completedProjects=" + completedProjects +
                ", totalRevenue=" + totalRevenue +
                ", totalEmployees=" + totalEmployees +
                ", totalClients=" + totalClients +
                ", totalAdmins=" + totalAdmins +
                '}';
    }
} 