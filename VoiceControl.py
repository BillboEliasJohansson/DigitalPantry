import json
import sounddevice as sd
from scipy.io.wavfile import write
import wavio as wv
from openai import OpenAI

def remove_item(item, amount, file_contents):
    food_dict = {}

    if "§" in file_contents:
        pairs = file_contents.split('§')
    else:
        pairs = [file_contents]

    for pair in pairs:
        key, value = pair.split(':')
        food_dict[key] = int(value)

    item = item.capitalize()

    if item in food_dict:
        food_dict[item] -= int(amount)
        if food_dict[item] <= 0:
            del food_dict[item]

    result_pairs = [f"{key}:{value}" for key, value in food_dict.items()]
    # print for debugging
    print("removed " + item)
    return '§'.join(result_pairs)

def add_item(item, amount, file_contents):
    food_dict = {}

    if "§" in file_contents:
        pairs = file_contents.split('§')
    else:
        pairs = [file_contents]

    for pair in pairs:
        key, value = pair.split(':')
        food_dict[key] = int(value)

    item = item.capitalize()

    if item in food_dict:
        food_dict[item] += int(amount)
    else:
        # Assign new key-value pair to the dictionary
        food_dict[item] = int(amount)

    result_pairs = [f"{key}:{value}" for key, value in food_dict.items()]
    # print for debugging
    print("Added " + item)
    return '§'.join(result_pairs)

file_path = 'pantry.txt'
freq = 44100
duration = 5
amount = "1"

print("Recording now")
recording = sd.rec(int(duration * freq), samplerate=freq, channels=2)
sd.wait()
print("Stopped recording")

write("VoiceCommand.wav", freq, recording)
try:
    client = OpenAI()
except Exception as e:
    print(e)
    
audio_file = open("VoiceCommand.wav", "rb")

transcript = client.audio.transcriptions.create(
    model="whisper-1",
    file=audio_file,
    response_format="text",
)

result = json.dumps(transcript, ensure_ascii=False).lower()

removeKeywords = ["ta bort", "remove"]
addKeywords = ["lägg till", "add"]

# print for debugging
print(result)

numberCall = client.chat.completions.create(
    model="gpt-3.5-turbo",
    messages=[
        {"role": "system", "content": "Svara endast med en siffra i nummerformat till exempel 1,2,3,4,5,6,7,8,9,10: svara annars med null om strängen inte innehåller en siffra i textform till exempel ett, två, tre, fyra, fem"},
        {"role": "user", "content": "Vilken av dessa ord i strängen: " + result + ": är en siffra till exempel ett, två, tre, fyra, fem"}
    ]
)

numberResponse = numberCall.choices[0].message.content
# print for debugging
print(numberResponse)

if numberResponse != "null":
    amount = numberResponse

for word in removeKeywords:
    if word in result:
        result = result.replace(word, "")
        try:
            with open(file_path, 'r') as file:
                file_contents = file.read()
                file_contents_temp = file_contents.lower()

            for item in file_contents_temp.split("§"):
                key, value = item.split(":")
                if key in result:
                    file_contents = remove_item(key, amount, file_contents)

            with open(file_path, 'w') as file:
                file.write(file_contents)

        except FileNotFoundError:
            print(f"File not found: {file_path}")
        except Exception as e:
            print(f"An error occurred: {e}")
        break

found = False

for word in addKeywords:
    if word in result:
        result = result.replace(word, "")
        try:
            with open(file_path, 'r') as file:
                file_contents = file.read()
                file_contents_temp = file_contents.lower()

            for item in file_contents_temp.split("§"):
                key, value = item.split(":")
                if key in result:
                    found = True
                    file_contents = add_item(key, amount, file_contents)

            if not found:
                response = client.chat.completions.create(
                model="gpt-3.5-turbo",
                messages=[
                    {"role": "system", "content": "Svara med endast ett ord eller ingenting"},
                    {"role": "user", "content": "Vilket av dessa ord i strängen: " + result + ": är något du kan köpa på en matbutik"}
                ]
                )
                content = response.choices[0].message.content
                # print for debugging
                print(content)
                file_contents = add_item(content, amount, file_contents)

            with open(file_path, 'w') as file:
                file.write(file_contents)

        except FileNotFoundError:
            print(f"File not found: {file_path}")
        except Exception as e:
            print(f"An error occurred: {e}")
        break
