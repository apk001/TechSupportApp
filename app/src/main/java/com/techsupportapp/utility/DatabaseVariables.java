package com.techsupportapp.utility;

public class DatabaseVariables {

    public static final String FIREBASE_URL = "https://infsysprojteam2-abbdf.firebaseio.com/";

    public static class FullPath {

        public static class Users {
            public static final String DATABASE_ALL_USER_TABLE = "user_table";

            public static final String DATABASE_UNVERIFIED_USER_TABLE = "user_table/unverified_table";
            public static final String DATABASE_VERIFIED_USER_TABLE = "user_table/verified_table";

            public static final String DATABASE_VERIFIED_SIMPLE_USER_TABLE = "user_table/verified_table/simple_user_table";
            public static final String DATABASE_VERIFIED_SPECIALIST_TABLE = "user_table/verified_table/worker_table";
            public static final String DATABASE_VERIFIED_CHIEF_TABLE = "user_table/verified_table/chief_table";
            public static final String DATABASE_VERIFIED_MANAGER_TABLE = "user_table/verified_table/manager_table";
        }

        public static class Tickets {
            public static final String DATABASE_ALL_TICKET_TABLE = "ticket_table";
            public static final String DATABASE_UNMARKED_TICKET_TABLE = "ticket_table/unmarked_table";
            public static final String DATABASE_MARKED_TICKET_TABLE = "ticket_table/marked_table";
            public static final String DATABASE_SOLVED_TICKET_TABLE = "ticket_table/solved_table";
        }

        public static class Indexes {
            public static final String DATABASE_TICKET_INDEX_COUNTER = "static_variables_table/ticket_index_counter";
            public static final String DATABASE_USER_INDEX_COUNTER = "static_variables_table/user_index_counter";
            public static final String DATABASE_FIRST_DATE_INDEX = "static_variables_table/first_date_index";
            public static final String DATABASE_LAST_DATE_INDEX = "static_variables_table/last_date_index";
        }
    }

    public static class ExceptFolder {

        public static class Users {
            public static final String DATABASE_UNVERIFIED_USER_TABLE = "unverified_table";
            public static final String DATABASE_VERIFIED_USER_TABLE = "verified_table";

            public static final String DATABASE_VERIFIED_SIMPLE_USER_TABLE = "verified_table/simple_user_table";
            public static final String DATABASE_VERIFIED_SPECIALIST_TABLE = "verified_table/worker_table";
            public static final String DATABASE_VERIFIED_CHIEF_TABLE = "verified_table/chief_table";
            public static final String DATABASE_VERIFIED_MANAGER_TABLE = "verified_table/manager_table";

            public static final String DATABASE_SIMPLE_USER_TABLE = "simple_user_table";
            public static final String DATABASE_SPECIALIST_TABLE = "worker_table";
            public static final String DATABASE_CHIEF_TABLE = "chief_table";
            public static final String DATABASE_MANAGER_TABLE = "manager_table";
        }

        public static class Tickets {
            public static final String DATABASE_UNMARKED_TICKET_TABLE = "unmarked_table";
            public static final String DATABASE_MARKED_TICKET_TABLE = "marked_table";
            public static final String DATABASE_SOLVED_TICKET_TABLE = "solved_table";
        }

        public static class Indexes {
            public static final String DATABASE_TICKET_INDEX_COUNTER = "ticket_index_counter";
            public static final String DATABASE_USER_INDEX_COUNTER = "user_index_counter";
            public static final String DATABASE_FIRST_DATE_INDEX = "first_date_index";
            public static final String DATABASE_LAST_DATE_INDEX = "last_date_index";
        }
    }
}
