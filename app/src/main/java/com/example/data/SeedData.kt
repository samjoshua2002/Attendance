package com.example.data

object SeedData {
    val INITIAL_EMPLOYEES = listOf(
        Employee("sam", "Sam", "Developer", "sam@skillskapes.com", true, "sam123", "av_logo_1"),
        Employee("vaishu", "Vaishu", "Developer", "vaishu@skillskapes.com", true, "vaishu123", "av_logo_2"),
        Employee("loki", "Loki", "Developer", "loki@skillskapes.com", true, "loki123", "av_logo_3"),
        Employee("aathi", "Aathi", "Designer", "aathi@skillskapes.com", true, "aathi123", "av_logo_4"),
        Employee("naomi", "Naomi", "Designer", "naomi@skillskapes.com", true, "naomi123", "av_logo_5"),
        // Sharmi was a developer who quit, represented here as inactive (per prompt)
        Employee("sharmi", "Sharmi", "Developer", "sharmi@skillskapes.com", false, "sharmi123", "av_logo_6"),
        Employee("jojo", "Jojo", "Boss", "jojo@skillskapes.com", true, "jojo123", "av_boss_1"),
        Employee("priyanka", "Priyanka", "Boss", "priyanka@skillskapes.com", true, "priyanka123", "av_boss_2"),
        Employee("samjoshua", "Sam Joshua", "Developer", "samjoshua.skillskapes@gmail.com", true, "joshua123", "av_logo_sam")
    )

    val INITIAL_MEETINGS = listOf(
        Meeting(0, "Daily Sync-up (Morning)", "23/05/2026", "10:15 AM", "https://meet.google.com/abc-defg-hij", "Boss Ananya", "Discuss chair occupancy and today's design reviews."),
        Meeting(0, "Daily Check-out (Evening)", "23/05/2026", "06:00 PM", "https://meet.google.com/klm-nopq-rst", "Boss Rohan", "Evening status reporting and sprint planning.")
    )

    fun parseSeedAttendance(): List<AttendanceRecord> {
        val records = mutableListOf<AttendanceRecord>()
        RAW_DATA.trim().lines().forEach { line ->
            if (line.isBlank()) return@forEach
            val tokens = line.trim().split(Regex("\\s+"))
            if (tokens.size < 2) return@forEach

            val date = tokens[0]
            val dayOfWeek = tokens[1]
            
            val p1Raw = if (tokens.size > 2) tokens[2] else null
            val p2Raw = if (tokens.size > 3) tokens[3] else null
            val p3Raw = if (tokens.size > 4) tokens[4] else null

            val p1 = if (p1Raw.equals("null", ignoreCase = true)) null else p1Raw
            val p2 = if (p2Raw.equals("null", ignoreCase = true)) null else p2Raw
            val p3 = if (p3Raw.equals("null", ignoreCase = true)) null else p3Raw

            val absent = if (tokens.size > 5) {
                tokens.subList(5, tokens.size).joinToString(" ")
            } else {
                null
            }

            records.add(
                AttendanceRecord(
                    date = date,
                    dayOfWeek = dayOfWeek,
                    p1 = p1,
                    p2 = p2,
                    p3 = p3,
                    absent = absent
                )
            )
        }
        return records
    }

    private val RAW_DATA = """
15/10/2025 Wednesday Sam Naomi Null Vaishu
16/10/2025 Thursday Loki Sam Naomi Vaishu
17/10/2025 Friday Loki Sharmi Naomi
18/10/2025 Saturday Sam Naomi Sharmi
21/10/2025 Tuesday Aathi Loki Naomi
22/10/2025 Wednesday Aathi Naomi Null Sam
23/10/2025 Thursday Loki Sam Naomi
24/10/2025 Friday Naomi Sharmi Sam
27/10/2025 Monday Aathi Sharmi Sam Vaishu
28/10/2025 Tuesday Aathi Naomi Sam
29/10/2025 Wednesday Aathi Naomi Loki
30/10/2025 Thursday Aathi Loki Sharmi
31/10/2025 Friday Vaishu Sharmi Sam
01/11/2025 Saturday Naomi Vaishu Loki
03/11/2025 Monday Vaishu Sharmi Naomi
04/11/2025 Tuesday Vaishu Sam Loki
05/11/2025 Wednesday Aathi Vaishu Naomi
06/11/2025 Thursday Aathi Loki Sam
07/11/2025 Friday Aathi Naomi Sharmi
10/11/2025 Monday Aathi Vaishu Null Loki
11/11/2025 Tuesday Aathi Vaishu Sam
12/11/2025 Wednesday Naomi Aathi Sharmi
13/11/2025 Thursday Sharmi Sam Loki
14/11/2025 Friday Loki Sam Naomi
15/11/2025 Saturday Sharmi Naomi Loki
17/11/2025 Monday Loki Sam Naomi
18/11/2025 Tuesday Sam Naomi Sharmi
19/11/2025 Wednesday Sharmi Aathi Loki
20/11/2025 Thursday Aathi Sam Loki
21/11/2025 Friday Sharmi Naomi Loki
24/11/2025 Monday Sharmi Sam Loki
25/11/2025 Tuesday Sharmi Sam Naomi
26/11/2025 Wednesday Sharmi Naomi Loki
27/11/2025 Thursday Loki Aathi Naomi
28/11/2025 Friday Naomi Aathi Loki
29/11/2025 Saturday Sam Aathi Sharmi
01/12/2025 Monday Vaishu Sam Naomi
02/12/2025 Tuesday Vaishu Naomi Loki
03/12/2025 Wednesday Vaishu Sam Naomi
04/12/2025 Thursday Sam Naomi Loki
05/12/2025 Friday Sam Loki Aathi
06/12/2025 Saturday Vaishu Aathi Loki
08/12/2025 Monday Vaishu Aathi Sam
09/12/2025 Tuesday Vaishu Aathi Loki
10/12/2025 Wednesday Naomi Aathi Loki
11/12/2025 Thursday Aathi Sam Vaishu
12/12/2025 Friday Sam Naomi Loki
15/12/2025 Monday Vaishu Sam Naomi
16/12/2025 Tuesday Vaishu Sam Aathi
17/12/2025 Wednesday Loki Naomi Aathi
18/12/2025 Thursday Vaishu Naomi Aathi
19/12/2025 Friday Loki Naomi Sam
20/12/2025 Saturday Vaishu Loki Naomi
22/12/2025 Monday Sam Vaishu Naomi
23/12/2025 Tuesday Sam Loki Naomi
24/12/2025 Wednesday Vaishu Loki Naomi
26/12/2025 Friday Vaishu Loki Naomi sam,aathi
29/12/2025 Monday Aathi Sam Vaishu naomi
30/12/2025 Tuesday Aathi Sam Loki naomi
31/12/2025 Wednesday Aathi Loki Vaishu sam,naomi
02/01/2026 Friday Sam Vaishu Aathi naomi
03/01/2026 Saturday Loki Aathi Naomi
05/01/2026 Monday Loki Aathi Sam
06/01/2026 Tuesday Naomi Loki Aathi
07/01/2026 Wednesday Loki Vaishu Naomi
08/01/2026 Thursday Sam Vaishu Naomi
09/01/2026 Friday Sam Vaishu Naomi
12/01/2026 Monday Sam Loki Naomi
13/01/2026 Tuesday Vaishu Sam Naomi
14/01/2026 Wednesday Sam Vaishu Naomi
16/01/2026 Friday Loki Sam Naomi Vaishu
17/01/2026 Saturday Loki Sam Naomi Aathi,Vaishu
19/01/2026 Monday Vaishu Loki Naomi
20/01/2026 Tuesday Vaishu Loki Naomi
21/01/2026 Wednesday Vaishu Naomi Aathi
22/01/2026 Thursday Loki Sam Aathi
23/01/2026 Friday Sam Aathi Vaishu
27/01/2026 Tuesday Sam Naomi Vaishu
28/01/2026 Wednesday Sam Naomi Vaishu
29/01/2026 Thursday Loki Aathi Naomi
30/01/2026 Friday Loki Aathi Sam
31/01/2026 Saturday Loki Aathi Vaishu
02/02/2026 Monday Vaishu Aathi Sam
03/02/2026 Tuesday Loki Aathi Vaishu
04/02/2026 Wednesday Loki Aathi Naomi Sam
05/02/2026 Thursday Loki Aathi Vaishu Sam
06/02/2026 Friday Loki Naomi Sam
07/02/2026 Saturday Vaishu Naomi Sam
09/02/2026 Monday Vaishu Naomi Sam
10/02/2026 Tuesday Vaishu Naomi Loki
11/02/2026 Wednesday Vaishu Naomi Sam
12/02/2026 Thursday Vaishu Naomi Loki
13/02/2026 Friday Sam Naomi Loki
16/02/2026 Monday Aathi Sam Vaishu
17/02/2026 Tuesday Aathi Sam Loki
18/02/2026 Wednesday Aathi Sam Loki
19/02/2026 Thursday Aathi Sam Vaishu
20/02/2026 Friday Naomi Loki Vaishu
21/02/2026 Saturday Naomi Loki Vaishu
23/02/2026 Monday Naomi Aathi Loki
24/02/2026 Tuesday Sam Aathi Loki
25/02/2026 Wednesday Aathi Sam Vaishu
26/02/2026 Thursday Naomi Aathi Sam
27/02/2026 Friday Naomi Loki Vaishu
02/03/2026 Monday Null Sam Vaishu Naomi(sick_leave)
03/03/2026 Tuesday Naomi Sam Vaishu
04/03/2026 Wednesday Naomi Sam Vaishu
05/03/2026 Thursday Naomi Loki Vaishu
06/03/2026 Friday Aathi Loki Vaishu
07/03/2026 Saturday Aathi Loki Naomi vaishu
09/03/2026 Monday Aathi Vaishu Sam
10/03/2026 Tuesday Aathi Vaishu Naomi
11/03/2026 Wednesday Aathi Sam Loki
12/03/2026 Thursday Naomi Aathi Sam
13/03/2026 Friday Naomi Loki Vaishu
16/03/2026 Monday Naomi Sam Vaishu loki
17/03/2026 Tuesday Naomi Sam Loki
18/03/2026 Wednesday Naomi Vaishu Loki
19/03/2026 Thursday Naomi Sam Loki
20/03/2026 Friday Naomi Sam Vaishu
21/03/2026 Saturday Naomi Vaishu Loki
23/03/2026 Monday Naomi Loki Sam
24/03/2026 Tuesday Naomi Vaishu Loki aathi
25/03/2026 Wednesday Naomi Loki Sam aathi
26/03/2026 Thursday Aathi Vaishu Sam
27/03/2026 Friday Aathi Vaishu Sam
30/03/2026 Monday Aathi Vaishu Sam
31/03/2026 Tuesday Aathi Vaishu Loki
01/04/2026 Wednesday Aathi Loki Sam
02/04/2026 Thursday Aathi Naomi Sam
04/04/2026 Saturday Aathi Naomi Loki vaishu
06/04/2026 Monday Aathi Sam Null loki
07/04/2026 Tuesday Aathi Sam Vaishu
08/04/2026 Wednesday Aathi Sam Naomi
09/04/2026 Thursday Aathi Vaishu Naomi
10/04/2026 Friday Naomi Vaishu Loki
13/04/2026 Monday Naomi Sam Loki
15/04/2026 Wednesday Aathi Vaishu Loki
16/04/2026 Thursday Aathi Sam Loki
17/04/2026 Friday Aathi Vaishu Sam
18/04/2026 Saturday Naomi Vaishu Loki
20/04/2026 Monday Aathi Vaishu Sam
21/04/2026 Tuesday Aathi Naomi Loki
22/04/2026 Wednesday Naomi Loki Vaishu
23/04/2026 Thursday Null Null Null leave_or_wfh_election
24/04/2026 Friday Naomi Vaishu Sam
27/04/2026 Monday Naomi Loki Sam vaishu
28/04/2026 Tuesday Naomi Loki Sam
29/04/2026 Wednesday Naomi Vaishu Loki sam
30/04/2026 Thursday Naomi Vaishu Sam
02/05/2026 Saturday Naomi Vaishu Loki
04/05/2026 Monday Naomi Vaishu Sam
05/05/2026 Tuesday Naomi Vaishu Sam
06/05/2026 Wednesday Naomi Loki Sam
07/05/2026 Thursday Naomi Vaishu Loki
08/05/2026 Friday Naomi Vaishu Loki
11/05/2026 Monday Naomi Loki Sam
12/05/2026 Tuesday Naomi Loki Sam
13/05/2026 Wednesday Aathi Vaishu Sam
14/05/2026 Thursday Aathi Vaishu Sam
15/05/2026 Friday Aathi Vaishu Loki
16/05/2026 Saturday Naomi Vaishu Loki
18/05/2026 Monday Naomi Null Sam vaishu_loki
19/05/2026 Tuesday Naomi Vaishu Loki
20/05/2026 Wednesday Aathi Sam Loki
21/05/2026 Thursday Aathi Vaishu Loki
22/05/2026 Friday Aathi Sam Vaishu
25/05/2025 Monday Naomi Loki Sam
26/05/2025 Tuesday Naomi Loki Vaishu
27/05/2025 Wednesday Naomi Sam Vaishu
29/05/2025 Friday Naomi Sam Vaishu
30/05/2025 Saturday Aathi Null Vaishu sam_or_loki_discussion
    """
}
