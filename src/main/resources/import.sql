INSERT INTO public.domain(active, integration_id, "name")VALUES(TRUE,'global','global');
INSERT INTO public.domain(active, integration_id, "name")VALUES(TRUE,'09ff3d105f231000b12e3572f2b4775d','Default');

-- *********** AUDIT ***********

-- DROP TABLE public.sys_user_audit;

CREATE TABLE public.sys_user_audit
(
    id bigint NOT NULL DEFAULT nextval('sys_user_audit_id_seq'::regclass),
    active boolean NOT NULL,
    create_at date,
    email character varying(255) COLLATE pg_catalog."default",
    employee_number character varying(255) COLLATE pg_catalog."default",
    first_name character varying(255) COLLATE pg_catalog."default",
    integration_id character varying(255) COLLATE pg_catalog."default",
    last_name character varying(255) COLLATE pg_catalog."default",
    name character varying(255) COLLATE pg_catalog."default",
    password character varying(255) COLLATE pg_catalog."default",
    solver boolean DEFAULT false,
    user_name character varying(255) COLLATE pg_catalog."default",
    vip boolean DEFAULT false,
    company bigint,
    department bigint,
    domain bigint,
    location bigint,
    solver_password character varying(255) COLLATE pg_catalog."default",
    locked boolean DEFAULT false,
    manager character varying(255) COLLATE pg_catalog."default",
    mobile_phone character varying(255) COLLATE pg_catalog."default",
    code character varying(255) COLLATE pg_catalog."default",
    created_on timestamp without time zone,
    updated_on timestamp without time zone
)

TABLESPACE pg_default;

ALTER TABLE public.sys_user_audit
    OWNER to postgres;