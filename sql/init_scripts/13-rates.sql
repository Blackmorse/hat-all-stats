CREATE FUNCTION IF NOT EXISTS rates AS (rating, opposite_rating) -> (pow(rating / 4, 3) / (pow(rating / 4, 3) + pow(opposite_rating / 4, 3)))
