import sys

file_path = 'pantry.txt'
items_list = sys.argv[1:]

pairs = items_list.split('§')

# Create a dictionary from the pairs
food_dict = {}

for pair in pairs:
    # Split each pair into key and value using ':' as a separator
    key, value = pair.split(':')
    # Assign key-value pair to the dictionary
    food_dict[key] = int(value)

try:
    with open(file_path, 'r') as file:
        # Read the contents of the file
        file_contents = file.read()
except FileNotFoundError:
    print(f"File not found: {file_path}")
except Exception as e:
    print(f"An error occurred: {e}")

if file_contents:
    existing_pairs = file_contents.split('§')

    for pair in existing_pairs:
        # Split each pair into key and value using ':' as a separator
        key, value = pair.split(':')
        # If the key already exists, subtract the new value from the existing value
        if key in food_dict:
            food_dict[key] -= int(value)
            # If the result is less than or equal to 0, remove the pair
            if food_dict[key] <= 0:
                del food_dict[key]

# Convert the dictionary to a list of pairs with '§' between each pair
result_pairs = [f"{key}:{value}" for key, value in food_dict.items()]

# Join the pairs with '§' and print the result
result_string = '§'.join(result_pairs)
print(result_string)

# Open the file in write mode and write the updated dictionary
with open(file_path, 'w') as file:
    # Write the result pairs with '§' between each pair
    file.write('§'.join(result_pairs))