package com.example.data

object SeedData {
    val INITIAL_EMPLOYEES = listOf(
        Employee(
            id = "sam_001",
            name = "Sam",
            role = "Superadmin",
            email = "samjoshua.skillskapes@gmail.com",
            isSuperAdmin = true
        ),
        Employee(
            id = "priyanka_002",
            name = "Priyanka",
            role = "Boss",
            email = "ceo@skillskapes.com"
        ),
        Employee(
            id = "naomi_003",
            name = "Naomi",
            role = "Developer",
            email = "naomi@skillskapes.com"
        ),
        Employee(
            id = "loki_004",
            name = "Loki",
            role = "Developer",
            email = "loki@skillskapes.com"
        ),
        Employee(
            id = "vaishu_005",
            name = "Vaishu",
            role = "Designer",
            email = "vaishu@skillskapes.com"
        ),
        Employee(
            id = "aathi_006",
            name = "Aathi",
            role = "Developer",
            email = "aathi@skillskapes.com"
        )
    )

    val INITIAL_MEETINGS = emptyList<Meeting>()

    fun parseSeedAttendance(): List<AttendanceRecord> {
        return listOf(
            // Testing Record
            AttendanceRecord("22/05/2026", "Friday", "Sam", "Vaishu", "Aathi", null, null),
            
            // New Cycle Starting Monday
            AttendanceRecord("25/05/2026", "Monday", "Naomi", "Loki", "Sam", null, null),
            AttendanceRecord("26/05/2026", "Tuesday", "Naomi", "Loki", "Vaishu", null, null),
            AttendanceRecord("27/05/2026", "Wednesday", "Naomi", "Sam", "Vaishu", null, null),
            AttendanceRecord("29/05/2026", "Friday", "Naomi", "Sam", "Vaishu", null, null),
            AttendanceRecord("30/05/2026", "Saturday", "Aathi", "Sam", "Vaishu", null, null)
        )
    }
}
