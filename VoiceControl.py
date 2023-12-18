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
        food_dict[key] += int(amount)
    else:
        # Assign key-value pair to the dictionary
        food_dict[key] = int(value)

    result_pairs = [f"{key}:{value}" for key, value in food_dict.items()]
    return '§'.join(result_pairs)

file_path = 'pantry.txt'
freq = 44100
duration = 5
amountToRemove = "1"

print("Recording now")
recording = sd.rec(int(duration * freq), samplerate=freq, channels=2)
sd.wait()
print("Stopped recording")

write("test2.wav", freq, recording)

client = OpenAI()

audio_file = open("test2.wav", "rb")

transcript = client.audio.transcriptions.create(
    model="whisper-1",
    file=audio_file,
    response_format="text",
)

result = json.dumps(transcript, ensure_ascii=False).lower()

removeKeywords = ["ta bort", "remove"]
addKeywords = ["lägg till", "add"]

print(result)


for word in removeKeywords:
    if word in result:
        print(word.lower())
        try:
            with open(file_path, 'r') as file:
                file_contents = file.read()
                file_contents_temp = file_contents.lower()

            for item in file_contents_temp.split("§"):
                key, value = item.split(":")
                if key in result:
                    file_contents = remove_item(key, amountToRemove, file_contents)

            with open(file_path, 'w') as file:
                file.write(file_contents)

        except FileNotFoundError:
            print(f"File not found: {file_path}")
        except Exception as e:
            print(f"An error occurred: {e}")
        break

for word in addKeywords:
    if word in result:
        print(word.lower())
        try:
            with open(file_path, 'r') as file:
                file_contents = file.read()
                file_contents_temp = file_contents.lower()

            for item in file_contents_temp.split("§"):
                key, value = item.split(":")
                if key in result:
                    file_contents = add_item(key, amountToRemove, file_contents)

            with open(file_path, 'w') as file:
                file.write(file_contents)

        except FileNotFoundError:
            print(f"File not found: {file_path}")
        except Exception as e:
            print(f"An error occurred: {e}")
        break