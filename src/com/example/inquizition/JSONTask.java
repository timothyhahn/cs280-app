package com.example.inquizition;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import android.app.Activity;
import android.os.AsyncTask;

abstract class JSONTask extends AsyncTask<Void, Void, Void> {

	Object result;
	String urlstr;
	Activity callback;
	

	
	public JSONTask(Activity callback, String urlstr)
	{
		this.callback = callback;
		this.urlstr = urlstr;
	}
	
	private String getJSONStr(String urlstr) throws IOException
	{
		URL url = new URL(urlstr);
		HttpURLConnection request = null;
		request = (HttpURLConnection)url.openConnection();
		request.connect();
	
		InputStreamReader is = new InputStreamReader(request.getInputStream());
		BufferedReader reader = new BufferedReader(is);
		StringBuilder sb = new StringBuilder();
		
		while(reader.ready())
		{
			sb.append(reader.readLine());	
		}
		
		return sb.toString();
		
	}
	
	public Object getResults()
	{
		
		return result;
	}

	@Override
	protected Void doInBackground(Void... params)
	{
		String json = null;
		
		try {
			json = getJSONStr(urlstr);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		parseJson(json);

		return null;
		
	}
	
	@Override
	protected void onPostExecute(Void params)
	{
		doneTask();
	}
	
	abstract void doneTask();
	abstract void parseJson(String json);
	
}


class GetQuizNameTask extends JSONTask
{

	QuizCreator callback;
	
	public GetQuizNameTask(Activity aCallback, String urlstr) {
		super(aCallback, urlstr);
		callback = (QuizCreator)aCallback;
		
	}

	@Override
	void parseJson(String json) {

		this.result = json;
	}

	@Override
	void doneTask() {
		
		callback.gotQuizName();
		
	}
	
}



class GetRunningQuizzesTask extends JSONTask
{
	ArrayList<QuizGame> quizGames;
	JoinActivity callback;
	public GetRunningQuizzesTask(Activity aCallback, String urlstr) {
		
		super(aCallback, urlstr);
		callback = (JoinActivity)aCallback;
		
	}

	@Override
	void doneTask() {
		
		callback.gotRunningQuizzes();
		
	}

	@Override
	void parseJson(String json) {
		
		Gson gson = new Gson();
		JsonObject j = gson.fromJson(json, JsonObject.class);
		quizGames = new ArrayList<QuizGame>();
	
			JsonArray quizzes = null;
			
			try
			{
				quizzes = j.get("quizzes").getAsJsonArray();
				
				for(int i = 0; i < quizzes.size(); i++)
				{
					JsonElement je = quizzes.get(i);
					QuizGame quizGame = gson.fromJson(je, QuizGame.class);
					quizGames.add(quizGame);
				}
			}

			catch(NullPointerException e)
			{
			}
			

			finally
			{
				result = quizGames;
			}
			
			
		
	}
}

class GetQuizTask extends JSONTask
{
	JoinActivity callback;
	public GetQuizTask(Activity aCallback, String urlstr) {
		super(aCallback, urlstr);
		callback = (JoinActivity)aCallback;
	}
				
	@Override
	void doneTask() {
		
		callback.gotQuiz();
	}
	
	@Override
	void parseJson(String json) {
		
		Gson gson = new Gson();
		if(json.length() > 5)
		{
			JsonObject j = gson.fromJson(json, JsonObject.class);
			QuizGame quizGame = gson.fromJson(j, QuizGame.class);
			result = quizGame;
		}
		
		else
		{
			result = null;
		}
		
		
	}
}



class GetSecondsLeftTask extends JSONTask
{
	JoinActivity callback;
	int taskIndex;
	int id;
	
	public GetSecondsLeftTask(Activity aCallback, String urlstr, int id, int taskIndex) {
		super(aCallback, urlstr);
		this.taskIndex = taskIndex;
		this.id = id;
		callback = (JoinActivity)aCallback;
		
	}

	@Override
	void doneTask() {
		
		callback.gotSecondsLeft(taskIndex);
		
	}

	@Override
	void parseJson(String json) {
	
		result = new int[]{id, Integer.parseInt(json)};
	}
	
	
}


class GetQuizResultsTask extends JSONTask
{

	ResultsActivity callback;
	
	public GetQuizResultsTask(Activity callback, String urlstr) {
		super(callback, urlstr);
		this.callback = (ResultsActivity)callback;
	}

	@Override
	void doneTask() {
		
		callback.gotResults();
		
	}

	@Override
	void parseJson(String json) {
		
		Gson gson = new Gson();
		
		JsonObject j = gson.fromJson(json, JsonObject.class);
		JsonArray ja = j.get("results").getAsJsonArray();
		ArrayList<Result> results = new ArrayList<Result>();
		
		for(int i = 0; i < ja.size(); i++)
		{
			JsonElement je = ja.get(i);
			Result quizResult = gson.fromJson(je, Result.class);
			results.add(quizResult);
		}

		result = results;
	}
	
}
