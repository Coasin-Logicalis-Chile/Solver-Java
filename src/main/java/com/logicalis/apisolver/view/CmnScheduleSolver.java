package com.logicalis.apisolver.view;

import javax.persistence.Column;
import java.io.Serializable;

public class CmnScheduleSolver implements Serializable {
    private String parent;
    private String document;
    @Column(length = 80000)
    private String description;
    private String sys_updated_on;
    private String type;
    private String document_key;
    private String sys_class_name;
    private String sys_id;
    private String sys_updated_by;
    private String plural_label;
    private String read_only;
    private String sys_created_on;
    private String sys_name;
    private String sys_created_by;
    private String sys_mod_count;
    private String is_legacy_schedule;
    private String label;
    private String calendar_name;
    private String sys_domain_path;
    private String sys_tags;
    private String time_zone;
    private String sys_update_name;
    private String name;
    private String sys_policy;

    public String getParent() {
        return parent;
    }

    public void setParent(String parent) {
        this.parent = parent;
    }

    public String getDocument() {
        return document;
    }

    public void setDocument(String document) {
        this.document = document;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getSys_updated_on() {
        return sys_updated_on;
    }

    public void setSys_updated_on(String sys_updated_on) {
        this.sys_updated_on = sys_updated_on;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getDocument_key() {
        return document_key;
    }

    public void setDocument_key(String document_key) {
        this.document_key = document_key;
    }

    public String getSys_class_name() {
        return sys_class_name;
    }

    public void setSys_class_name(String sys_class_name) {
        this.sys_class_name = sys_class_name;
    }

    public String getSys_id() {
        return sys_id;
    }

    public void setSys_id(String sys_id) {
        this.sys_id = sys_id;
    }

    public String getSys_updated_by() {
        return sys_updated_by;
    }

    public void setSys_updated_by(String sys_updated_by) {
        this.sys_updated_by = sys_updated_by;
    }

    public String getPlural_label() {
        return plural_label;
    }

    public void setPlural_label(String plural_label) {
        this.plural_label = plural_label;
    }

    public String getRead_only() {
        return read_only;
    }

    public void setRead_only(String read_only) {
        this.read_only = read_only;
    }

    public String getSys_created_on() {
        return sys_created_on;
    }

    public void setSys_created_on(String sys_created_on) {
        this.sys_created_on = sys_created_on;
    }

    public String getSys_name() {
        return sys_name;
    }

    public void setSys_name(String sys_name) {
        this.sys_name = sys_name;
    }

    public String getSys_created_by() {
        return sys_created_by;
    }

    public void setSys_created_by(String sys_created_by) {
        this.sys_created_by = sys_created_by;
    }

    public String getSys_mod_count() {
        return sys_mod_count;
    }

    public void setSys_mod_count(String sys_mod_count) {
        this.sys_mod_count = sys_mod_count;
    }

    public String getIs_legacy_schedule() {
        return is_legacy_schedule;
    }

    public void setIs_legacy_schedule(String is_legacy_schedule) {
        this.is_legacy_schedule = is_legacy_schedule;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getCalendar_name() {
        return calendar_name;
    }

    public void setCalendar_name(String calendar_name) {
        this.calendar_name = calendar_name;
    }

    public String getSys_domain_path() {
        return sys_domain_path;
    }

    public void setSys_domain_path(String sys_domain_path) {
        this.sys_domain_path = sys_domain_path;
    }

    public String getSys_tags() {
        return sys_tags;
    }

    public void setSys_tags(String sys_tags) {
        this.sys_tags = sys_tags;
    }

    public String getTime_zone() {
        return time_zone;
    }

    public void setTime_zone(String time_zone) {
        this.time_zone = time_zone;
    }

    public String getSys_update_name() {
        return sys_update_name;
    }

    public void setSys_update_name(String sys_update_name) {
        this.sys_update_name = sys_update_name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSys_policy() {
        return sys_policy;
    }

    public void setSys_policy(String sys_policy) {
        this.sys_policy = sys_policy;
    }
}
