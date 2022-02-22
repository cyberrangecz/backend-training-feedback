ALTER TABLE graph DROP CONSTRAINT graph_training_definition_id_trainee_id_graph_type_key;
ALTER TABLE trainee ALTER COLUMN sandbox_id DROP NOT NULL;

DROP INDEX trainee_sandbox_index;
DROP INDEX graph_index;

-- THREE PARTIAL INDEXES FOR INDIVIDUAL GRAPH TYPES
-- REFERENCE GRAPH
CREATE UNIQUE INDEX reference_graph_index ON graph (training_definition_id, graph_type)
    WHERE graph.training_instance_id IS NULL AND trainee_id IS NULL;
-- SUMMARY GRAPH
CREATE UNIQUE INDEX summary_graph_index ON graph (training_definition_id, training_instance_id, graph_type)
    WHERE trainee_id IS NULL;
-- TRAINEE GRAPH
CREATE UNIQUE INDEX trainee_graph_index ON graph (training_definition_id, training_instance_id, trainee_id, graph_type);