CREATE TABLE trainee (
                         trainee_id      bigserial    NOT NULL PRIMARY KEY,
                         training_run_id bigint       NOT NULL,
                         user_ref_id     bigint       NOT NULL,
                         sandbox_id      varchar (36)       UNIQUE,
                         UNIQUE (training_run_id)
);

CREATE TABLE graph (
    graph_id                bigserial        NOT NULL PRIMARY KEY,
    training_definition_id  int8             NOT NULL,
    training_instance_id    int8             NULL,
    graph_type              varchar(63)      NOT NULL,
    label                   varchar(255)     NOT NULL,
    label_location          varchar(255)     NOT NULL,
    font_size               double precision NOT NULL,
    trainee_id              int8             NULL,
    FOREIGN KEY (trainee_id) REFERENCES trainee,
    UNIQUE (training_definition_id, trainee_id, graph_type)

);

CREATE TABLE sub_graph (
    sub_graph_id bigserial    NOT NULL PRIMARY KEY,
    graph_id     int8         NOT NULL,
    label        varchar(255) NOT NULL,
    color        varchar(255) NOT NULL,
    FOREIGN KEY (graph_id) REFERENCES graph
);

CREATE TABLE edge (
    edge_id      bigserial        NOT NULL PRIMARY KEY,
    sub_graph_id int8             NOT NULL,
    from_node    varchar(255)     NOT NULL,
    to_node      varchar(255)     NOT NULL,
    weight       bigint           NOT NULL,
    length       double precision NOT NULL,
    type         varchar(255),
    tool         varchar(255),
    color        varchar(255)     NOT NULL,
    style        varchar(255)     NOT NULL,
    FOREIGN KEY (sub_graph_id) REFERENCES sub_graph
);

CREATE TABLE options (
    edge_id int8         NOT NULL,
    option  varchar(255) NOT NULL,
    PRIMARY KEY (edge_id, option),
    FOREIGN KEY (edge_id) REFERENCES edge,
    UNIQUE (edge_id, option)
);

CREATE TABLE level (
    level_id     bigserial NOT NULL PRIMARY KEY,
    trainee_id   int8      NOT NULL,
    level_ref_id bigint    NOT NULL,
    start_time   timestamp NOT NULL,
    end_time     timestamp,
    FOREIGN KEY (trainee_id) REFERENCES trainee
);
CREATE TABLE mistake (
    mistake_id   bigserial    NOT NULL PRIMARY KEY,
    mistake_type varchar(255) NOT NULL
);

CREATE TABLE command (
    command_id      bigserial    NOT NULL PRIMARY KEY ,
    level_id        int8         NOT NULL,
    mistake_id      int8         NULL,
    timestamp       timestamp    NOT NULL,
    training_time   bigint       NOT NULL,
    cmd             varchar(255) NOT NULL,
    command_type    varchar(255) NOT NULL,
    options         varchar(255),
    uname           varchar(255),
    wd              varchar(255),
    from_host_ip    varchar(255),
    FOREIGN KEY (level_id) REFERENCES level,
    FOREIGN KEY (mistake_id) REFERENCES mistake
);

CREATE TABLE node (
    node_id      bigserial    NOT NULL PRIMARY KEY,
    sub_graph_id int8         NOT NULL,
    label        varchar(255) NOT NULL,
    name         varchar(255),
    color        varchar(255) NOT NULL,
    shape        varchar(255) NOT NULL,
    FOREIGN KEY (sub_graph_id) REFERENCES sub_graph
);

CREATE INDEX command_level_index ON command (level_id);
CREATE INDEX edge_sub_graph_index ON edge (sub_graph_id);
CREATE INDEX level_trainee_index ON level (trainee_id);
CREATE INDEX mistake_type_index ON mistake (mistake_type);

-- THREE PARTIAL INDEXES FOR INDIVIDUAL GRAPH TYPES
-- REFERENCE GRAPH
CREATE UNIQUE INDEX reference_graph_index ON graph (training_definition_id, graph_type)
    WHERE graph.training_instance_id IS NULL AND trainee_id IS NULL;
-- SUMMARY GRAPH
CREATE UNIQUE INDEX summary_graph_index ON graph (training_definition_id, training_instance_id, graph_type)
    WHERE trainee_id IS NULL;
-- TRAINEE GRAPH
CREATE UNIQUE INDEX trainee_graph_index ON graph (training_definition_id, training_instance_id, trainee_id, graph_type);

CREATE SEQUENCE command_id_seq AS bigint INCREMENT 50 MINVALUE 1;
CREATE SEQUENCE edge_id_seq AS bigint INCREMENT 50 MINVALUE 1;
CREATE SEQUENCE graph_id_seq AS bigint INCREMENT 50 MINVALUE 1;
CREATE SEQUENCE level_id_seq AS bigint INCREMENT 50 MINVALUE 1;
CREATE SEQUENCE mistake_id_seq AS bigint INCREMENT 50 MINVALUE 1;
CREATE SEQUENCE node_id_seq AS bigint INCREMENT 50 MINVALUE 1;
CREATE SEQUENCE sub_graph_id_seq AS bigint INCREMENT 50 MINVALUE 1;
CREATE SEQUENCE trainee_id_seq AS bigint INCREMENT 50 MINVALUE 1;
