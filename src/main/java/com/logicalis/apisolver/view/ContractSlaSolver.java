package com.logicalis.apisolver.view;

import java.io.Serializable;

public class ContractSlaSolver implements Serializable {
    private String active;
  //  @Column(length = 1000)
  //  private String cancel_condition;
    private String collection;
    //private String condition_class;
    private String duration;
    private String duration_type;
    private String enable_logging;
    private String name;
   // @Column(length = 1000)
   // private String pause_condition;
    private String relative_duration_works_on;
  private String reset_action;
   // @Column(length = 1000)
  //  private String reset_condition;
  //  @Column(length = 1000)
  //  private String resume_condition;
    private String retroactive;
    private String retroactive_pause;
    private String schedule_source;
    private String schedule_source_field;
   // @Column(length = 1000)
    //private String start_condition;
   // @Column(length = 1000)
   // private String stop_condition;
    private String sys_class_name;
    private String sys_created_by;
    private String sys_created_on;
    private String sys_id;
    private String sys_mod_count;
    private String sys_name;
    private String sys_policy;
    private String sys_tags;
    private String sys_update_name;
    private String sys_updated_by;
    private String sys_updated_on;
    private String target;
    private String timezone;
    private String timezone_source;
    private String type;
    private String vendor;
    private String when_to_cancel;
    private String when_to_resume;
    public String getActive() {
        return active;
    }

    public void setActive(String active) {
        this.active = active;
    }

    /*public String getCancel_condition() {
        return cancel_condition;
    }

    public void setCancel_condition(String cancel_condition) {
        this.cancel_condition = cancel_condition;
    }*/

    public String getCollection() {
        return collection;
    }

    public void setCollection(String collection) {
        this.collection = collection;
    }

   /* public String getCondition_class() {
        return condition_class;
    }

    public void setCondition_class(String condition_class) {
        this.condition_class = condition_class;
    }
*/
    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public String getDuration_type() {
        return duration_type;
    }

    public void setDuration_type(String duration_type) {
        this.duration_type = duration_type;
    }

    public String getEnable_logging() {
        return enable_logging;
    }

    public void setEnable_logging(String enable_logging) {
        this.enable_logging = enable_logging;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    /*public String getPause_condition() {
        return pause_condition;
    }

    public void setPause_condition(String pause_condition) {
        this.pause_condition = pause_condition;
    }*/

    public String getRelative_duration_works_on() {
        return relative_duration_works_on;
    }

    public void setRelative_duration_works_on(String relative_duration_works_on) {
        this.relative_duration_works_on = relative_duration_works_on;
    }

    public String getReset_action() {
        return reset_action;
    }

    public void setReset_action(String reset_action) {
        this.reset_action = reset_action;
    }

   /* public String getReset_condition() {
        return reset_condition;
    }

    public void setReset_condition(String reset_condition) {
        this.reset_condition = reset_condition;
    }

    public String getResume_condition() {
        return resume_condition;
    }

    public void setResume_condition(String resume_condition) {
        this.resume_condition = resume_condition;
    }*/

    public String getRetroactive() {
        return retroactive;
    }

    public void setRetroactive(String retroactive) {
        this.retroactive = retroactive;
    }

    public String getRetroactive_pause() {
        return retroactive_pause;
    }

    public void setRetroactive_pause(String retroactive_pause) {
        this.retroactive_pause = retroactive_pause;
    }

    public String getSchedule_source() {
        return schedule_source;
    }

    public void setSchedule_source(String schedule_source) {
        this.schedule_source = schedule_source;
    }

    public String getSchedule_source_field() {
        return schedule_source_field;
    }

    public void setSchedule_source_field(String schedule_source_field) {
        this.schedule_source_field = schedule_source_field;
    }
/*
    public String getStart_condition() {
        return start_condition;
    }

    public void setStart_condition(String start_condition) {
        this.start_condition = start_condition;
    }

    public String getStop_condition() {
        return stop_condition;
    }

    public void setStop_condition(String stop_condition) {
        this.stop_condition = stop_condition;
    }
*/
    public String getSys_class_name() {
        return sys_class_name;
    }

    public void setSys_class_name(String sys_class_name) {
        this.sys_class_name = sys_class_name;
    }

    public String getSys_created_by() {
        return sys_created_by;
    }

    public void setSys_created_by(String sys_created_by) {
        this.sys_created_by = sys_created_by;
    }

    public String getSys_created_on() {
        return sys_created_on;
    }

    public void setSys_created_on(String sys_created_on) {
        this.sys_created_on = sys_created_on;
    }

    public String getSys_id() {
        return sys_id;
    }

    public void setSys_id(String sys_id) {
        this.sys_id = sys_id;
    }

    public String getSys_mod_count() {
        return sys_mod_count;
    }

    public void setSys_mod_count(String sys_mod_count) {
        this.sys_mod_count = sys_mod_count;
    }

    public String getSys_name() {
        return sys_name;
    }

    public void setSys_name(String sys_name) {
        this.sys_name = sys_name;
    }

    public String getSys_policy() {
        return sys_policy;
    }

    public void setSys_policy(String sys_policy) {
        this.sys_policy = sys_policy;
    }

    public String getSys_tags() {
        return sys_tags;
    }

    public void setSys_tags(String sys_tags) {
        this.sys_tags = sys_tags;
    }

    public String getSys_update_name() {
        return sys_update_name;
    }

    public void setSys_update_name(String sys_update_name) {
        this.sys_update_name = sys_update_name;
    }

    public String getSys_updated_by() {
        return sys_updated_by;
    }

    public void setSys_updated_by(String sys_updated_by) {
        this.sys_updated_by = sys_updated_by;
    }

    public String getSys_updated_on() {
        return sys_updated_on;
    }

    public void setSys_updated_on(String sys_updated_on) {
        this.sys_updated_on = sys_updated_on;
    }

    public String getTarget() {
        return target;
    }

    public void setTarget(String target) {
        this.target = target;
    }

    public String getTimezone() {
        return timezone;
    }

    public void setTimezone(String timezone) {
        this.timezone = timezone;
    }

    public String getTimezone_source() {
        return timezone_source;
    }

    public void setTimezone_source(String timezone_source) {
        this.timezone_source = timezone_source;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getVendor() {
        return vendor;
    }

    public void setVendor(String vendor) {
        this.vendor = vendor;
    }

    public String getWhen_to_cancel() {
        return when_to_cancel;
    }

    public void setWhen_to_cancel(String when_to_cancel) {
        this.when_to_cancel = when_to_cancel;
    }

    public String getWhen_to_resume() {
        return when_to_resume;
    }

    public void setWhen_to_resume(String when_to_resume) {
        this.when_to_resume = when_to_resume;
    }


}
