
SET statement_timeout = 0;
SET lock_timeout = 0;
SET idle_in_transaction_session_timeout = 0;
SET transaction_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = on;
SELECT pg_catalog.set_config('search_path', '', false);
SET check_function_bodies = false;
SET xmloption = content;
SET client_min_messages = warning;
SET row_security = off;



ALTER SCHEMA public OWNER TO pg_database_owner;



SET default_tablespace = '';

SET default_table_access_method = heap;


CREATE TABLE public.audit_events (
    id uuid NOT NULL,
    action character varying(255) NOT NULL,
    actor_id uuid,
    ip_address character varying(50),
    metadata jsonb,
    severity character varying(255) NOT NULL,
    target_entity character varying(255) NOT NULL,
    target_id uuid,
    tenant_id uuid,
    "timestamp" timestamp(6) with time zone NOT NULL,
    user_agent character varying(500),
    CONSTRAINT audit_events_action_check CHECK (((action)::text = ANY ((ARRAY['LOGIN_SUCCESS'::character varying, 'LOGIN_FAILED'::character varying, 'PASSWORD_RESET'::character varying, 'USER_DEACTIVATED'::character varying, 'ROLE_CHANGED'::character varying, 'TOKEN_ROTATED'::character varying, 'SESSION_REVOKED'::character varying, 'USER_INVITED'::character varying, 'USER_ACTIVATED'::character varying, 'INVITATION_ACCEPTED'::character varying, 'ACCOUNT_LOCKED'::character varying])::text[]))),
    CONSTRAINT audit_events_severity_check CHECK (((severity)::text = ANY ((ARRAY['INFO'::character varying, 'WARNING'::character varying, 'CRITICAL'::character varying])::text[]))),
    CONSTRAINT audit_events_target_entity_check CHECK (((target_entity)::text = ANY ((ARRAY['USER'::character varying, 'ROLE'::character varying, 'SESSION'::character varying, 'TOKEN'::character varying, 'TENANT'::character varying])::text[])))
);


CREATE TABLE public.invitation_tokens (
    id uuid DEFAULT gen_random_uuid() NOT NULL,
    created_at timestamp(6) without time zone NOT NULL,
    deleted boolean NOT NULL,
    updated_at timestamp(6) without time zone NOT NULL,
    version bigint NOT NULL,
    tenant_id uuid NOT NULL,
    expires_at timestamp(6) with time zone NOT NULL,
    token_hash character varying(255) NOT NULL,
    used boolean NOT NULL,
    used_at timestamp(6) with time zone,
    user_id uuid NOT NULL
);



CREATE TABLE public.password_reset_tokens (
    id uuid DEFAULT gen_random_uuid() NOT NULL,
    created_at timestamp(6) without time zone NOT NULL,
    deleted boolean NOT NULL,
    updated_at timestamp(6) without time zone NOT NULL,
    version bigint NOT NULL,
    expiry_date timestamp(6) with time zone NOT NULL,
    token character varying(255) NOT NULL,
    used boolean NOT NULL,
    user_id uuid DEFAULT gen_random_uuid() NOT NULL
);


CREATE TABLE public.permissions (
    id uuid DEFAULT gen_random_uuid() NOT NULL,
    created_at timestamp(6) without time zone NOT NULL,
    deleted boolean NOT NULL,
    updated_at timestamp(6) without time zone NOT NULL,
    version bigint NOT NULL,
    code character varying(60) NOT NULL,
    description text
);



CREATE TABLE public.refresh_tokens (
    id uuid DEFAULT gen_random_uuid() NOT NULL,
    created_at timestamp(6) without time zone NOT NULL,
    deleted boolean NOT NULL,
    updated_at timestamp(6) without time zone NOT NULL,
    version bigint NOT NULL,
    device_info text,
    expiry_date timestamp(6) with time zone NOT NULL,
    token character varying(255) NOT NULL,
    user_id uuid DEFAULT gen_random_uuid() NOT NULL,
    session_id uuid
);



CREATE TABLE public.role_permissions (
    role_id uuid DEFAULT gen_random_uuid() NOT NULL,
    permission_id uuid DEFAULT gen_random_uuid() NOT NULL
);



CREATE TABLE public.roles (
    id uuid DEFAULT gen_random_uuid() NOT NULL,
    created_at timestamp(6) without time zone NOT NULL,
    deleted boolean NOT NULL,
    updated_at timestamp(6) without time zone NOT NULL,
    version bigint NOT NULL,
    tenant_id uuid NOT NULL,
    name character varying(50) NOT NULL,
    CONSTRAINT roles_name_check CHECK (((name)::text = ANY ((ARRAY['OWNER'::character varying, 'MANAGER'::character varying, 'CASHIER'::character varying, 'INVENTORY_CLERK'::character varying, 'ACCOUNTANT'::character varying])::text[])))
);



CREATE TABLE public.tenants (
    id uuid DEFAULT gen_random_uuid() NOT NULL,
    created_at timestamp(6) without time zone NOT NULL,
    deleted boolean NOT NULL,
    updated_at timestamp(6) without time zone NOT NULL,
    version bigint NOT NULL,
    active boolean NOT NULL,
    code character varying(100) NOT NULL,
    currency_code character varying(10) NOT NULL,
    email character varying(150),
    legal_name character varying(200),
    name character varying(150) NOT NULL,
    phone character varying(30),
    tax_registration_number character varying(100),
    timezone character varying(50) NOT NULL
);



CREATE TABLE public.user_sessions (
    id uuid DEFAULT gen_random_uuid() NOT NULL,
    created_at timestamp(6) without time zone NOT NULL,
    deleted boolean NOT NULL,
    updated_at timestamp(6) without time zone NOT NULL,
    version bigint NOT NULL,
    tenant_id uuid NOT NULL,
    expires_at timestamp(6) with time zone NOT NULL,
    ip_address character varying(255),
    last_used_at timestamp(6) with time zone NOT NULL,
    refresh_token_hash character varying(255) NOT NULL,
    revoked boolean NOT NULL,
    revoked_at timestamp(6) with time zone,
    user_agent character varying(500),
    user_id uuid NOT NULL
);


CREATE TABLE public.users (
    id uuid DEFAULT gen_random_uuid() NOT NULL,
    created_at timestamp(6) without time zone NOT NULL,
    deleted boolean NOT NULL,
    updated_at timestamp(6) without time zone NOT NULL,
    version bigint NOT NULL,
    tenant_id uuid NOT NULL,
    active boolean DEFAULT true NOT NULL,
    email character varying(150) NOT NULL,
    email_verified boolean DEFAULT false NOT NULL,
    first_name character varying(80) NOT NULL,
    last_login_at timestamp with time zone,
    last_name character varying(80) NOT NULL,
    mfa_enabled boolean DEFAULT false NOT NULL,
    password_hash text NOT NULL,
    role_id uuid DEFAULT gen_random_uuid() NOT NULL,
    failed_login_attempts integer DEFAULT 0 NOT NULL,
    invited_at timestamp with time zone,
    locked boolean DEFAULT false NOT NULL,
    locked_at timestamp with time zone,
    status character varying(20) DEFAULT 'ACTIVE'::character varying NOT NULL,
    CONSTRAINT users_status_check CHECK (((status)::text = ANY ((ARRAY['INVITED'::character varying, 'ACTIVE'::character varying, 'DEACTIVATED'::character varying])::text[])))
);



CREATE TABLE public.verification_tokens (
    id uuid DEFAULT gen_random_uuid() NOT NULL,
    created_at timestamp(6) without time zone NOT NULL,
    deleted boolean NOT NULL,
    updated_at timestamp(6) without time zone NOT NULL,
    version bigint NOT NULL,
    expiry_date timestamp(6) with time zone NOT NULL,
    token character varying(255) NOT NULL,
    user_id uuid DEFAULT gen_random_uuid() NOT NULL
);



ALTER TABLE ONLY public.audit_events
    ADD CONSTRAINT audit_events_pkey PRIMARY KEY (id);




ALTER TABLE ONLY public.tenants
    ADD CONSTRAINT idx_tenants_code UNIQUE (code);



ALTER TABLE ONLY public.invitation_tokens
    ADD CONSTRAINT invitation_tokens_pkey PRIMARY KEY (id);



ALTER TABLE ONLY public.password_reset_tokens
    ADD CONSTRAINT password_reset_tokens_pkey PRIMARY KEY (id);



ALTER TABLE ONLY public.permissions
    ADD CONSTRAINT permissions_pkey PRIMARY KEY (id);



ALTER TABLE ONLY public.refresh_tokens
    ADD CONSTRAINT refresh_tokens_pkey PRIMARY KEY (id);



ALTER TABLE ONLY public.role_permissions
    ADD CONSTRAINT role_permissions_pkey PRIMARY KEY (role_id, permission_id);


ALTER TABLE ONLY public.roles
    ADD CONSTRAINT roles_pkey PRIMARY KEY (id);



ALTER TABLE ONLY public.tenants
    ADD CONSTRAINT tenants_pkey PRIMARY KEY (id);


ALTER TABLE ONLY public.verification_tokens
    ADD CONSTRAINT uk6q9nsb665s9f8qajm3j07kd1e UNIQUE (token);



ALTER TABLE ONLY public.password_reset_tokens
    ADD CONSTRAINT uk71lqwbwtklmljk3qlsugr1mig UNIQUE (token);



ALTER TABLE ONLY public.permissions
    ADD CONSTRAINT uk7lcb6glmvwlro3p2w2cewxtvd UNIQUE (code);



ALTER TABLE ONLY public.refresh_tokens
    ADD CONSTRAINT uk7tdcd6ab5wsgoudnvj7xf1b7l UNIQUE (user_id);



ALTER TABLE ONLY public.roles
    ADD CONSTRAINT uk_role_name_tenant UNIQUE (name, tenant_id);



ALTER TABLE ONLY public.users
    ADD CONSTRAINT uk_users_tenant_email UNIQUE (tenant_id, email);



ALTER TABLE ONLY public.verification_tokens
    ADD CONSTRAINT ukdqp95ggn6gvm865km5muba2o5 UNIQUE (user_id);



ALTER TABLE ONLY public.refresh_tokens
    ADD CONSTRAINT ukghpmfn23vmxfu3spu3lfg4r2d UNIQUE (token);




ALTER TABLE ONLY public.user_sessions
    ADD CONSTRAINT ukk33fiivedrep3oiaffwrh21pv UNIQUE (refresh_token_hash);



ALTER TABLE ONLY public.invitation_tokens
    ADD CONSTRAINT ukq0u7f9ks2ijajtc9ader1ftxi UNIQUE (token_hash);



ALTER TABLE ONLY public.user_sessions
    ADD CONSTRAINT user_sessions_pkey PRIMARY KEY (id);



ALTER TABLE ONLY public.users
    ADD CONSTRAINT users_pkey PRIMARY KEY (id);



ALTER TABLE ONLY public.verification_tokens
    ADD CONSTRAINT verification_tokens_pkey PRIMARY KEY (id);



CREATE INDEX idx_audit_action ON public.audit_events USING btree (action);



CREATE INDEX idx_audit_actor ON public.audit_events USING btree (actor_id);



CREATE INDEX idx_audit_tenant ON public.audit_events USING btree (tenant_id);



CREATE INDEX idx_audit_timestamp ON public.audit_events USING btree ("timestamp");


CREATE INDEX idx_invite_token ON public.invitation_tokens USING btree (token_hash);



CREATE INDEX idx_invite_user ON public.invitation_tokens USING btree (user_id);



CREATE INDEX idx_role_tenant_id ON public.roles USING btree (tenant_id);



CREATE INDEX idx_rt_token ON public.refresh_tokens USING btree (token);



CREATE INDEX idx_rt_user_id ON public.refresh_tokens USING btree (user_id);


CREATE INDEX idx_session_refresh_hash ON public.user_sessions USING btree (refresh_token_hash);


CREATE INDEX idx_session_tenant ON public.user_sessions USING btree (tenant_id);


CREATE INDEX idx_session_user ON public.user_sessions USING btree (user_id);



CREATE INDEX idx_tenants_active ON public.tenants USING btree (active);



CREATE INDEX idx_users_tenant_id ON public.users USING btree (tenant_id);



CREATE INDEX idx_users_tenant_role_id ON public.users USING btree (tenant_id, role_id);



CREATE INDEX idx_users_tenant_status ON public.users USING btree (tenant_id, status);


ALTER TABLE ONLY public.refresh_tokens
    ADD CONSTRAINT fk1lih5y2npsf8u5o3vhdb9y0os FOREIGN KEY (user_id) REFERENCES public.users(id);



ALTER TABLE ONLY public.verification_tokens
    ADD CONSTRAINT fk54y8mqsnq1rtyf581sfmrbp4f FOREIGN KEY (user_id) REFERENCES public.users(id);



ALTER TABLE ONLY public.role_permissions
    ADD CONSTRAINT fkegdk29eiy7mdtefy5c7eirr6e FOREIGN KEY (permission_id) REFERENCES public.permissions(id);



ALTER TABLE ONLY public.password_reset_tokens
    ADD CONSTRAINT fkk3ndxg5xp6v7wd4gjyusp15gq FOREIGN KEY (user_id) REFERENCES public.users(id);


ALTER TABLE ONLY public.role_permissions
    ADD CONSTRAINT fkn5fotdgk8d1xvo8nav9uv3muc FOREIGN KEY (role_id) REFERENCES public.roles(id);


ALTER TABLE ONLY public.users
    ADD CONSTRAINT fkp56c1712k691lhsyewcssf40f FOREIGN KEY (role_id) REFERENCES public.roles(id);


