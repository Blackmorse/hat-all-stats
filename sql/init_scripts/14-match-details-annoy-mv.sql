CREATE MATERIALIZED VIEW hattrick.match_details_annoy_mv TO hattrick.match_details_annoy
(
    `match_id` UInt64,
    `goals` UInt8,
    `enemy_goals` UInt8,
    `tactic_type` UInt8,
    `tactic_skill` UInt8,
    `opposite_tactic_type` UInt8,
    `opposite_tactic_skill` UInt8,
    `rating_indirect_set_pieces_def` UInt8,
    `rating_indirect_set_pieces_att` UInt8,
    `opposite_rating_indirect_set_pieces_def` UInt8,
    `opposite_rating_indirect_set_pieces_att` UInt8,
    `vector` Array(Float64)
) AS
SELECT
    match_id,
    goals,
    enemy_goals,
    tactic_type,
    tactic_skill,
    opposite_tactic_type,
    opposite_tactic_skill,
    rating_indirect_set_pieces_def,
    rating_indirect_set_pieces_att,
    opposite_rating_indirect_set_pieces_def,
    opposite_rating_indirect_set_pieces_att,
    [rates(rating_midfield, opposite_rating_midfield), rates(rating_left_def, opposite_rating_right_att), rates(rating_mid_def, opposite_rating_mid_att), rates(rating_right_def, opposite_rating_left_att)] AS vector
FROM hattrick.match_details
