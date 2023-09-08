package com.logicalis.apisolver.model.enums;

public enum SnTable {
    Domain {
        @Override
        public String get(  ) {
            return "sys_domain";
        }
    },
    Company {
        @Override
        public String get(  ) {
            return "company";
        }
    },
    Catalog {
        @Override
        public String get(  ) {
            return "sc_catalog";
        }
    },
    Location {
        @Override
        public String get(  ) {
            return "location";
        }
    },
    Department {
        @Override
        public String get(  ) {
            return "department";
        }
    },
    ScRequest {
        @Override
        public String get(  ) {
            return "sc_request";
        }
    },
    ScRequestItem {
        @Override
        public String get(  ) {
            return "sc_req_item";
        }
    },
    Incident {
        @Override
        public String get(  ) {
            return "incident";
        }
    },
    TaskSla {
        @Override
        public String get(  ) {
            return "task_sla";
        }
    },
    ScTask {
        @Override
        public String get(  ) {
            return "sc_task";
        }
    },
    SysAudit {
        @Override
        public String get(  ) {
            return "sys_audit";
        }
    },
    SysAttachment {
        @Override
        public String get(  ) {
            return "sys_attachment";
        }
    },
    ScCartItem {
        @Override
        public String get(  ) {
            return "sc_cart_item";
        }
    };

    public abstract String get();
}

