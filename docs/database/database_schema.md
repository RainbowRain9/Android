# Database Schema for nian.db

## Table: DREAM

| Column ID (cid) | Name | Type | Not Null | Default Value | Primary Key (pk) |
|---|---|---|---|---|---|
| 0 | _id | INTEGER | No |  | Yes |
| 1 | NAME | TEXT | No |  | No |
| 2 | USER_ID | TEXT | No |  | No |
| 3 | DESC | TEXT | No |  | No |
| 4 | SORT_INDEX | INTEGER | Yes |  | No |
| 5 | CREATE_TIME | INTEGER | No |  | No |
| 6 | UPDATE_TIME | INTEGER | No |  | No |
| 7 | FINISH | INTEGER | Yes |  | No |
| 8 | LOCK | INTEGER | Yes |  | No |
| 9 | PERCENT | INTEGER | Yes |  | No |
| 10 | TAGS | TEXT | No |  | No |
| 11 | IMAGE | TEXT | No |  | No |
| 12 | HIDE | INTEGER | Yes |  | No |
| 13 | BACKGROUND | TEXT | No |  | No |
| 14 | B_EXT1 | INTEGER | Yes |  | No |
| 15 | B_EXT2 | INTEGER | Yes |  | No |
| 16 | L_EXT1 | INTEGER | No |  | No |
| 17 | L_EXT2 | INTEGER | No |  | No |
| 18 | S_EXT1 | TEXT | No |  | No |
| 19 | S_EXT2 | TEXT | No |  | No |
| 20 | S_EXT3 | TEXT | No |  | No |
| 21 | S_EXT4 | TEXT | No |  | No |
| 22 | I_EXT1 | INTEGER | Yes |  | No |
| 23 | I_EXT2 | INTEGER | Yes |  | No |

## Table: LOCAL_USER

| Column ID (cid) | Name | Type | Not Null | Default Value | Primary Key (pk) |
|---|---|---|---|---|---|
| 0 | _id | INTEGER | No |  | Yes |
| 1 | NAME | TEXT | No |  | No |
| 2 | IMAGE | TEXT | No |  | No |
| 3 | CREATE_AT | INTEGER | No |  | No |

## Table: STEP

| Column ID (cid) | Name | Type | Not Null | Default Value | Primary Key (pk) |
|---|---|---|---|---|---|
| 0 | _id | INTEGER | No |  | Yes |
| 1 | DREAM_ID | INTEGER | No |  | No |
| 2 | IMAGES | TEXT | No |  | No |
| 3 | CONTENT | TEXT | No |  | No |
| 4 | CREATE_AT | INTEGER | No |  | No |
| 5 | UPDATE_AT | INTEGER | No |  | No |
| 6 | AT_TOP | INTEGER | Yes |  | No |
| 7 | HIDE | INTEGER | Yes |  | No |
| 8 | TYPE | INTEGER | Yes |  | No |
| 9 | TOO_BIG | INTEGER | Yes |  | No |
| 10 | COMMENT_COUNT | INTEGER | No |  | No |
| 11 | B_EXT1 | INTEGER | Yes |  | No |
| 12 | B_EXT2 | INTEGER | Yes |  | No |
| 13 | L_EXT1 | INTEGER | No |  | No |
| 14 | L_EXT2 | INTEGER | No |  | No |
| 15 | S_EXT1 | TEXT | No |  | No |
| 16 | S_EXT2 | TEXT | No |  | No |
| 17 | S_EXT3 | TEXT | No |  | No |
| 18 | S_EXT4 | TEXT | No |  | No |
| 19 | I_EXT1 | INTEGER | Yes |  | No |
| 20 | I_EXT2 | INTEGER | Yes |  | No |

## Table: android_metadata

| Column ID (cid) | Name | Type | Not Null | Default Value | Primary Key (pk) |
|---|---|---|---|---|---|
| 0 | locale | TEXT | No |  | No |

</rewritten_file> 