package fr.epita.assistant.jws.domain.entity;

public enum GameState
{
    FINISHED,
    RUNNING,
    STARTING;

    public static GameState StringToGameState(String string)
    {
        return GameState.valueOf(string);
    }
}
