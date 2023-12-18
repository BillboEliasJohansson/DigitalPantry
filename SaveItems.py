import sys

file_path = 'pantry.txt'
items_list = sys.argv[1]

pairs = items_list.split('§')

# Create a dictionary from the pairs
food_dict = {}

for pair in pairs:
    # Split each pair into key and value using ':' as a separator
    key, value_str = pair.split(':')

    # Convert value to float and round to the nearest whole number
    value = int(ceil(float(value_str)))

    # Assign key-value pair to the dictionary
    food_dict[key] = value

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
        pair_parts = pair.split(':')

        # Check if there are at least two parts (key and value)
        if len(pair_parts) >= 2:
            key, value = pair_parts[0], pair_parts[1]

            # If the key already exists, add the new value to the existing value
            if key in food_dict:
                food_dict[key] += int(value)
            else:
                # Assign key-value pair to the dictionary
                food_dict[key] = int(value)
        else:
            # Handle the case where there are not enough parts
            print(f"Skipping invalid pair: {pair}")

# Convert the dictionary to a list of pairs with '§' between each pair
result_pairs = [f"{key}:{value}" for key, value in food_dict.items()]

# Join the pairs with '§' and print the result
result_string = '§'.join(result_pairs)
print(result_string)

# Open the file in append mode and write the updated dictionary
with open(file_path, 'w') as file:
    # Write the result pairs with '§' between each pair
    file.write('§'.join(result_pairs))
