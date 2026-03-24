
package com.example.dicegame

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.edit

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Dice()
        }
    }
}
@Composable
fun Dice() {
    val context = LocalContext.current
    val prefs = remember { context.getSharedPreferences("my_prefs", Context.MODE_PRIVATE) }
    var showDice by remember { mutableIntStateOf(0) }
    var gameState by remember { mutableStateOf("") }
    var rolls by remember { mutableStateOf(mutableListOf<Int>()) }
    var gameMessage by remember { mutableStateOf("") }
    var wins by remember { mutableIntStateOf(0) }
    var losses by remember { mutableIntStateOf(0) }
    var dieIndex1 by remember { mutableIntStateOf(5) }
    var point by remember { mutableIntStateOf(0) }
    var dieIndex2 by remember { mutableIntStateOf(5) }
    var dieValue by remember { mutableIntStateOf(12) }
    val dieArray = arrayOf(
        R.drawable.dice1,
        R.drawable.dice2,
        R.drawable.dice3,
        R.drawable.dice4,
        R.drawable.dice5,
        R.drawable.dice6
    )

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(60.dp))
        Text(text = "Craps", fontSize = 50.sp, fontWeight = FontWeight.Bold)
        Text(
            "Instant Win:", fontSize = 20.sp, textDecoration = TextDecoration.Underline,
            textAlign = TextAlign.Start, modifier = Modifier.fillMaxWidth()
        )
        Text(
            "Roll a 7 or 11 on the opening roll.", fontSize = 20.sp,
            textAlign = TextAlign.Start, modifier = Modifier.fillMaxWidth()
        )
        Text(
            "Instant Loss:", fontSize = 20.sp, textDecoration = TextDecoration.Underline,
            textAlign = TextAlign.Start, modifier = Modifier.fillMaxWidth()
        )
        Text(
            "Roll a 2, 3, or 12 (Craps) on the opening roll.", fontSize = 20.sp,
            textAlign = TextAlign.Start, modifier = Modifier.fillMaxWidth()
        )
        Text(
            "The Point:", fontSize = 20.sp, textDecoration = TextDecoration.Underline,
            textAlign = TextAlign.Start, modifier = Modifier.fillMaxWidth()
        )
        Text(
            "Any other number becomes your 'point'. You must then keep rolling " +
                    "until you hit that same number to win—but if you roll a 7 first, you lose.",
            fontSize = 20.sp,
            textAlign = TextAlign.Start,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(15.dp))
        ScoreboardCard("Total Games", "Wins", "Losses")
        var totalgames = wins + losses

        ScoreboardCard(totalgames.toString(), wins.toString(), losses.toString())


        if (rolls.size>0) {
            Text(
                "Current game: ", fontSize = 25.sp, textDecoration = TextDecoration.Underline,
                textAlign = TextAlign.Start, modifier = Modifier.fillMaxWidth()
            )
        }
        if (point>0) {
            Text("Point: $point", fontSize = 25.sp,textAlign = TextAlign.Start, modifier = Modifier.fillMaxWidth())
        }
        if (rolls.size>0) {
            Text(
                "Rolls: " + printlist(rolls), fontSize = 25.sp,
                textAlign = TextAlign.Start, modifier = Modifier.fillMaxWidth()
            )
        }

        if (gameState != "done") {
            Button(enabled = enableButton(gameState), onClick = {
                dieIndex1 = rollDie()
                dieIndex2 = rollDie()
                showDice = 1
                dieValue = dieIndex1 + dieIndex2 + 2
                rolls.add(dieValue)
                if (point == 0) {
                    if (dieValue == 7 || dieValue == 11) {
                        gameState = "win"
                    } else if (dieValue == 2 || dieValue == 3 || dieValue == 12) {
                        gameState = "lose"
                    } else {
                        gameState = "continue"
                        point = dieValue
                    }
                } else {
                    if (dieValue == 7) {
                        gameState = "lose"
                    } else if (dieValue == point) {
                        gameState = "win"
                    } else {
                        gameState = "continue"
                    }
                }
            }
            )
            { Text("Roll Dice") }
        }


        if (gameState == "done") {
            Button(onClick = {
                point = 0
                showDice = 0
                gameState=""
                gameMessage=""
                rolls = mutableListOf<Int>()
            })
            { Text("Play Again") }
        }

        if (gameMessage.isNotEmpty()) {
            Text(gameMessage, fontSize = 25.sp)
        }

        if (showDice == 1) {
            Row() {
                Image(
                    painter = painterResource(id = dieArray[dieIndex1]),
                    contentDescription = "Dice image",
                    modifier = Modifier
                        .size(150.dp)
                )
                Spacer(modifier = Modifier.width(24.dp))
                Image(
                    painter = painterResource(id = dieArray[dieIndex2]),
                    contentDescription = "Dice image",
                    modifier = Modifier
                        .size(150.dp)
                )
            }
        }
        Spacer(modifier = Modifier.height(24.dp))

        if (gameState == "win") {
            gameMessage = "You rolled a $dieValue.  You win!"
            wins++
            gameState = "done"
        }
        if (gameState == "lose") {
            gameMessage = "You rolled a $dieValue.  You lose!"
            losses++
            gameState = "done"
        }
        if (gameState == "continue") {
            gameMessage = "You rolled a $dieValue.  Roll again."
        }

    }
}

fun rollDie():Int{
    val roll=(0..5).random()
    return roll
}

fun enableButton(gameState:String):Boolean{
    if (gameState=="done") {return false}
    else {return true}
}

fun printlist(lst:MutableList<Int>):String {
    if (lst.size==0) {return ""}
    var lst2 = ""
    for (l in lst) {
        lst2 = lst2 + l.toString() + " "
    }
    return lst2
}

@Composable
fun ScoreboardCard(col1:String, col2:String, col3:String,
                   modifier:Modifier=Modifier){
    val context= LocalContext.current
    Card(modifier=Modifier.fillMaxWidth()
        .padding(1.dp)) {
        Row() {
            Column(modifier = Modifier.weight(1f)
                .height(100.dp).fillMaxSize()
                .background(color=Color.Yellow)
                .padding(8.dp)
                .background(color = Color.Green)
                .border(4.dp, color = Color.Red),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally ) {
                Text(col1, fontWeight = FontWeight.Bold, fontSize = 30.sp)
            }
            Column(modifier = Modifier.weight(1f)
                .height(100.dp).fillMaxSize()
                .background(color=Color.Yellow)
                .padding(8.dp)
                .background(color = Color.Green)
                .border(4.dp, color = Color.Red),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally ) {
                Text(col2, fontWeight = FontWeight.Bold, fontSize = 30.sp)
            }
            Column(modifier = Modifier.weight(1f)
                .height(100.dp).fillMaxSize()
                .background(color=Color.Yellow)
                .padding(8.dp)
                .background(color = Color.Green)
                .border(4.dp, color = Color.Red),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(col3, fontWeight = FontWeight.Bold, fontSize = 30.sp)
            }
        }
    }
}
