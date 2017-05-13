from prison import Player
'''
This Python module models the three-player Prisoner's Dilemma game.
We use the integer "0" to represent cooperation, and "1" to represent 
defection. 

Recall that in the 2-players dilemma, U(DC) > U(CC) > U(DD) > U(CD), where
we give the payoff for the first player in the list. We want the three-player game 
to resemble the 2-player game whenever one player's response is fixed, and we
also want symmetry, so U(CCD) = U(CDC) etc. This gives the unique ordering

U(DCC) > U(CCC) > U(DDC) > U(CDC) > U(DDD) > U(CDD)

The payoffs for player 1 are given by the following matrix:

@author: akhtsang
Created on Feb 9, 2017
'''

import random
import math

PAYOFF = [[[6,3],[3,0]],[[8,5],[5,2]]]
NROUNDS = 100 

"""
So payoff[i][j][k] represents the payoff to player 1 when the first
player's action is i, the second player's action is j, and the
third player's action is k.
[]
In this simulation, triples of players will play each other repeatedly in a
'match'. A match consists of about 100 rounds, and your score from that match
is the average of the payoffs from each round of that match. For each round, your
strategy is given a list of the previous plays (so you can remember what your 
opponent did) and must compute the next action.
 """
    
class AmbushFromAllSides(Player):
    """
    This defines an interface for a player of the 3-player.  Inherit and modify this class
    by declaring the following:
    
    class SecretStrategyPlayer(Player)
        # code goes here
        # make sure you implement the play(...) function
    
    Attributes:
    While you are not prohibited from adding attributes.  You should not need 
    to implement do so.  The parameters to play(...) contain all information 
    available about the current state of play. 
    """
    
    def studentID(self):
        """ Returns the creator's numeric studentID """
        return "20477924"
    
    def agentName(self):
        """ Returns a creative name for the agent """
        return "Ambush From All Sides"
    
    def play(self, myHistory, oppHistory1, oppHistory2):
        """ 
        Given a history of play, computes and returns your next move
        ( 0 = cooperate; 1 = defect )
        myHistory = list of int representing your past plays
        oppHisotry1 = list of int representing opponent 1's past plays
        oppHisotry2 = list of int representing opponent 2's past plays
        NB: use len(myHistory) to find the number of games played
        """

        roundNum = len(oppHistory1)

        if roundNum == 0:
            # start off by cooperation
            return 0

        #recognize grim by seeing if there are at least 4 consecutive Defects
        opp1ConDefects = self.countConsecutiveDefect(oppHistory1)
        opp2ConDefects = self.countConsecutiveDefect(oppHistory2)
        #if grim strategy is identified, then always play Defect
        if opp1ConDefects >= 4 or opp2ConDefects >= 4:
            return 1

        #calculate probablity 
        opp1DefectProb = self.calculateProbability(oppHistory1)
        opp2DefectProb = self.calculateProbability(oppHistory2)

        if opp1DefectProb >= 0.7 and opp2DefectProb >= 0.7:
            #very bad impression
            return 1
        elif opp1DefectProb <= 0.15 and opp2DefectProb <= 0.15:
            #very good impression, cooperate
            return 0
        else:
            selfConsecutiveDefect = self.countConsecutiveDefect(myHistory)
            opp1TotalDefects = self.countTotalDefect(oppHistory1)
            opp2TotalDefects = self.countTotalDefect(oppHistory2)
            minDefect = 0
            if opp1TotalDefects > opp2TotalDefects:
                if opp2TotalDefects != 0:
                    minDefect = opp2TotalDefects
            else:
                minDefect = opp1TotalDefects

            if selfConsecutiveDefect == minDefect:
                # try to lure for cooperation once punishment is completed
                return 0
            else:
                # punish the same number of times as minimal defects
                return 1

    def countTotalDefect(self, historyList):
        defectCount = 0
        for item in historyList:
            defectCount += item

        return defectCount

    def calculateProbability(self, historyList):
        # percentage of defects within the total size of history list
        defectCount = self.countTotalDefect(historyList)

        return defectCount/len(historyList)

    def countConsecutiveDefect(self, historyList):
        # counts the consecutive Defects played in the latest rounds
        consecutiveDefect = 0
        for i in reversed(historyList):
            if i == 0:
                return consecutiveDefect

            consecutiveDefect += 1

        return consecutiveDefect
