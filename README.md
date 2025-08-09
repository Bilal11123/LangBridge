1. Download MBilal144/my-whisper-model/my_model.pt save it in my_model/ directory in the same directory as server.py
(./
---server.py
---my_model/
------------my_model.pt
)
-----
2. Then, uncomment line 59 and comment line 60
----
3. Make a LangBridge_DB database with the following Collections:
- Conversations
- Messages
- Users
-----
4. Install ffmpeg and add to the path
-----
5. Perform the following pip installs:
	pip install fastapi
	pip install uvicorn[standard]
	pip install pymongo
	pip install pydantic
	pip install numpy
	pip install speechrecognition
	pip install pydub
	pip install gTTS
	pip install openai-whisper
	pip install googletrans==4.0.0-rc1
	pip install nltk
	pip install transformers
	pip install torch
----
6. Add Geminin API key to chatbot/constant
