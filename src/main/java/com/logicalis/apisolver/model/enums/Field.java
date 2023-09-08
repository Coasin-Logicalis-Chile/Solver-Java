package com.logicalis.apisolver.model.enums;


public enum Field {
    AssignedTo {
        @Override
        public String get() {
            return "assigned_to";
        }

    },
    Category {
        @Override
        public String get() {
            return "category";
        }

    },
    CloseNotes {
        @Override
        public String get() {
            return "close_notes";
        }

    },
    Description {
        @Override
        public String get() {
            return "description";
        }

    },
    ShortDescription {
        @Override
        public String get() {
            return "short_description";
        }

    },
    ResolvedAt {
        @Override
        public String get() {
            return "resolved_at";
        }

    },
    State {
        @Override
        public String get() {
            return "state";
        }

    }, Stage {
        @Override
        public String get() {
            return "stage";
        }

    },
    Priority {
        @Override
        public String get() {
            return "priority";
        }


    },
    Impact {
        @Override
        public String get() {
            return "impact";
        }


    },
    Urgency {
        @Override
        public String get() {
            return "urgency";
        }


    },
    IncidentState {
        @Override
        public String get() {
            return "incident_state";
        }


    },
    ReasonPending {
        @Override
        public String get() {
            return "reason_pending";
        }

    },
    CloseCode {
        @Override
        public String get() {
            return "close_code";
        }


    },
    AssignmentGroup {
        @Override
        public String get() {
            return "assignment_group";
        }
    },
    ResolvedBy {
        @Override
        public String get() {
            return "resolved_by";
        }
    },
    ScalingGroup {
        @Override
        public String get() {
            return "u_scaling_group";
        }
    },
    SolverFlagResolvedBy {
        @Override
        public String get() {
            return "u_solver_flag_resolved_by";
        }
    },
    SolverFlagAssignedTo {
        @Override
        public String get() {
            return "u_solver_flag_assigned_to";
        }
    },
    SolverFlagClosedBy {
        @Override
        public String get() {
            return "u_solver_flag_closed_by";
        }
    },
    PendingOtherGroup {
        @Override
        public String get() {
            return "u_pending_other_group";
        }
    },
    NeedsAttention {
        @Override
        public String get() {
            return "needs_attention";
        }
    },
    MadeSla {
        @Override
        public String get() {
            return "made_sla";
        }
    },
    Knowledge {
        @Override
        public String get() {
            return "knowledge";
        }
    },
    Active {
        @Override
        public String get() {
            return "active";
        }
    },
    TaskFor {
        @Override
        public String get() {
            return "task_for";
        }
    },
    CallerId {
        @Override
        public String get() {
            return "caller_id";
        }
    },
    businessDuration {
        @Override
        public String get() {
            return "business_duration";
        }
    },
    calendarDuration {
        @Override
        public String get() {
            return "calendar_duration";
        }
    },
    CalendarStc {
        @Override
        public String get() {
            return "calendar_stc";
        }
    },
    TimeWorked {
        @Override
        public String get() {
            return "time_worked";
        }
    },
    BusinessStc {
        @Override
        public String get() {
            return "business_stc";
        }
    },
    CmdbCi {
        @Override
        public String get() {
            return "cmdb_ci";
        }
    },
    BusinessService {
        @Override
        public String get() {
            return "business_service";
        }
    },
    SysId {
        @Override
        public String get() {
            return "sys_id";
        }
    };

    public abstract String get();
}

