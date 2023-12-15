file_path = 'pantry.txt'

try:
    with open(file_path, 'r') as file:
        # Read the contents of the file
        file_contents = file.read()

        # Remove ':' and '§', and print each pair on a new line
        for pair in file_contents.split('§'):
            key, value = pair.split(':')
            print(f"{key}: {value}")

except FileNotFoundError:
    print(f"File not found: {file_path}")
except Exception as e:
    print("")
