CREATE TABLE trainee (
    trainee_id bigserial NOT NULL PRIMARY KEY,
    sandbox_id bigint NOT NULL
);
ALTER SEQUENCE trainee_trainee_id_seq RENAME TO trainee_id_seq;
ALTER SEQUENCE trainee_id_seq increment 50;

CREATE TABLE graph (
    graph_id bigserial NOT NULL PRIMARY KEY,
    trainee_id int8 NOT NULL,
    label varchar(255) NOT NULL,
    label_location varchar(255) NOT NULL,
    font_size double precision NOT NULL,
    FOREIGN KEY (trainee_id) REFERENCES trainee
);
ALTER SEQUENCE graph_graph_id_seq RENAME TO graph_id_seq;
ALTER SEQUENCE graph_id_seq increment 50;

CREATE TABLE sub_graph (
    sub_graph_id bigserial NOT NULL PRIMARY KEY,
    graph_id int8 NOT NULL,
    label varchar(255) NOT NULL,
    color varchar(255) NOT NULL,
    FOREIGN KEY (graph_id) REFERENCES graph
);
ALTER SEQUENCE sub_graph_sub_graph_id_seq RENAME TO sub_graph_id_seq;
ALTER SEQUENCE sub_graph_id_seq increment 50;

CREATE TABLE edge (
    edge_id bigserial NOT NULL PRIMARY KEY,
    sub_graph_id int8 NOT NULL,
    from_node varchar(255) NOT NULL,
    to_node varchar(255) NOT NULL,
    weight bigint NOT NULL,
    length double precision NOT NULL,
    type varchar(255),
    tool varchar(255),
    color varchar(255) NOT NULL,
    style varchar(255) NOT NULL,
    FOREIGN KEY (sub_graph_id) REFERENCES sub_graph
);
ALTER SEQUENCE edge_edge_id_seq RENAME TO edge_id_seq;
ALTER SEQUENCE edge_id_seq increment 50;

CREATE TABLE options (
    edge_id int8 NOT NULL PRIMARY KEY,
    option varchar(255) NOT NULL,
    FOREIGN KEY (edge_id) REFERENCES edge
);

CREATE TABLE level (
    level_id bigserial NOT NULL PRIMARY KEY,
    trainee_id int8 NOT NULL,
    level_ref_id bigint NOT NULL,
    start_time timestamp NOT NULL,
    end_time timestamp,
    FOREIGN KEY (trainee_id) REFERENCES trainee
);
ALTER SEQUENCE level_level_id_seq RENAME TO level_id_seq;
ALTER SEQUENCE level_id_seq increment 50;

CREATE TABLE mistake (
    mistake_id bigserial NOT NULL PRIMARY KEY,
    trainee_id int8 NOT NULL,
    mistake_type varchar(255) NOT NULL,
    FOREIGN KEY (trainee_id) REFERENCES trainee
);
ALTER SEQUENCE mistake_mistake_id_seq RENAME TO mistake_id_seq;
ALTER SEQUENCE mistake_id_seq increment 50;

CREATE TABLE command (
    command_id bigserial NOT NULL,
    level_id int8 NOT NULL,
    mistake_id int8 NOT NULL,
    timestamp timestamp NOT NULL,
    cmd varchar(255) NOT NULL,
    command_type varchar(255) NOT NULL,
    options varchar(255),
    uname varchar(255),
    wd varchar(255),
    from_host_ip varchar(255),
    FOREIGN KEY (level_id) REFERENCES level,
    FOREIGN KEY (mistake_id) REFERENCES mistake
);
ALTER SEQUENCE command_command_id_seq RENAME TO command_id_seq;
ALTER SEQUENCE command_id_seq increment 50;

CREATE TABLE node (
    node_id bigserial NOT NULL PRIMARY KEY,
    sub_graph_id int8 NOT NULL,
    label varchar(255) NOT NULL,
    name varchar(255),
    color varchar(255) NOT NULL,
    shape varchar(255) NOT NULL,
    FOREIGN KEY (sub_graph_id) REFERENCES sub_graph
);
ALTER SEQUENCE node_node_id_seq RENAME TO node_id_seq;
ALTER SEQUENCE node_id_seq increment 50;

CREATE INDEX command_level_index ON command (level_id);
CREATE INDEX edge_sub_graph_index ON edge (sub_graph_id);
CREATE INDEX level_trainee_index ON level (trainee_id);
CREATE INDEX mistake_type_index ON mistake (mistake_type);
CREATE UNIQUE INDEX trainee_sandbox_index ON trainee (sandbox_id);
