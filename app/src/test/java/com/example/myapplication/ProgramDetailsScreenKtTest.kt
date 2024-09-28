package com.example.myapplication

import com.example.myapplication.components.programs.createAnnotatedLinkString
import net.jqwik.api.Example

class ProgramDetailsScreenKtTest {

    val text = "Progress for Bodybuilding Exercises\n" +
            "\n" +
            "Dynamic Double Progression (<a href=\"https://www.youtube.com/watch?v=qHnVlltCVIs\">click link to watch video if you're not familiar</a>) is to be used on all bodybuilding exercises. Effort is the name of the name of the game here. When you reach the top end of the sets and reps, add weight and repeat in the same rep range. The volume that you get here is a huge driver of progress for your program, so put your best foot forward. (Follow the progressions otherwise detailed in the program)\n" +
            "\n" +
            "How to Select Movements\n" +
            "\n" +
            "The Basics\n" +
            "\n" +
            "There is almost no wrong answer. Effort, consistency, calories, sleep. These are all things you need to worry about a lot more than the specific exercise you're picking. If the target muscles are being worked, you picked the right exercise.\n" +
            "\n" +
            "<a href=\"https://www.youtube.com/watch?v=UoiWzC7AWCU\">Video</a>\n" +
            "\n" +
            "How to Bias Specific Muscles\n" +
            "\n" +
            "Once you understand the basics, you may want to bias certain muscles to bring up your physique or to improve your strengtth. Watch the applicable video in my Final Answer Playlist to assist with that.\n" +
            "\n" +
            "<a href=\"https://www.youtube.com/watch?v=UoiWzC7AWCU\">Video</a>\n" +
            "\n" +
            "Program FAQ\n" +
            "\n" +
            "What is RPE?\n" +
            "\n" +
            "It's Giga brain for \"leave some reps in the tank\" RPE 7 means you left 3 reps in the tank, RPE 8 means you left 2 reps , RPE 9 is 1 rep in the tank, and RPE 10 is zero reps left in the tank\n" +
            "\n" +
            "    RPE 10 = Concentric Failure, you physically cannot do another rep after trying\n" +
            "\n" +
            "    RPE 9 = Could have grinded out another rep\n" +
            "\n" +
            "    RPE 8 = Hard, but could have done two more reps\n" +
            "\n" +
            "    RPE 7 = Fairly challenging, could have done 3 more reps\n" +
            "\n" +
            "    RPE 6 = Feels like a warm up set, could have done 4 more reps, great for deloads\n" +
            "\n" +
            "I Over/Undershot My RPE. HELP\n" +
            "\n" +
            "If you overshoot, that's cool. Just make your back off sets a little bit easier than what is calculated for you. If you undershoot, make your back offs a little harder, OR just bump up weight and go for another top set.\n" +
            "\n" +
            "Can I add more volume in general?\n" +
            "\n" +
            "If you want to add volume to the program, add it to the bodybuilding portion AS NEEDED (not as WANTED). Before adding volume, ensure that the reps you are doing are of HIGH quality first.\n" +
            "\n" +
            "What is Myorep match?\n" +
            "\n" +
            "Myorep match is a Giga brain way of saying \"use rest pauses on your second and third sets so that they can match your first set's Rep count\" Use the same exercise for your Myorep match sets.\n" +
            "\n" +
            "How do I pick my top set weights?\n" +
            "\n" +
            "Do warm up sets of however many reps you have prescribed for the day, adding chunks of weight each time until you get to about 80-90% of whatever number you have in mind for the day. If your last warm up feels and looks junky, go conservative, if it feels and looks good, go for that number you had in mind.\n" +
            "\n" +
            "How often do I swap movements?\n" +
            "\n" +
            "Once you can no longer beat the books (add weight, reps, sets) and follow a double progression - IF AND ONLY IF you're eating enough, sleeping enough, hydrating, and getting your electrolytes in. If you are failing to do any of these to the best of your ability, you need to fix those first. If you're doing everything you need to do outside of the gym to support your training, you'll find that you need to swap hypertrophy movements every 6-8 weeks.\n" +
            "\n" +
            "What are my rest days?\n" +
            "\n" +
            "It's a standard Upper, Lower, rest, upper, lower, rest x2. You can add more rest as needed."

    @Example
    fun createAnnotatedLinkStringTest() {
        println("ProgramDetailsScreenKtTest: " + createAnnotatedLinkString(text, null))
    }
}