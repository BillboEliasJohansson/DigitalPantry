file_path = 'pantry.txt'

try:
    with open(file_path, 'r') as file:
        # Read the contents of the file
        file_contents = file.read()

        # Print the contents of the file
        print(file_contents)
except FileNotFoundError:
    print(f"File not found: {file_path}")
except Exception as e:
    print(f"An error occurred: {e}")