import sys

def remove_item(item, amount):

    file_path = 'pantry.txt'

    # Create a dictionary from the pairs
    food_dict = {}

    try:
        with open(file_path, 'r') as file:
            # Read the contents of the file
            file_contents = file.read()
    except FileNotFoundError:
        print(f"File not found: {file_path}")
    except Exception as e:
        print(f"An error occurred: {e}")

    if file_contents:

        if "§" in file_contents:
            pairs = file_contents.split('§')
        else:
            pairs = file_contents

        for pair in pairs:
            # Split each pair into key and value using ':' as a separator
            key, value = pair.split(':')
            # Assign key-value pair to the dictionary
            food_dict[key] = int(value)

    item = key.capitalize()

    if item in food_dict:
        food_dict[item] -= int(value)
        # If the result is less than or equal to 0, remove the pair
        if food_dict[item] <= 0:
            del food_dict[item]

    # Convert the dictionary to a list of pairs with '§' between each pair
    result_pairs = [f"{key}:{value}" for key, value in food_dict.items()]

    # Join the pairs with '§' and print the result
    result_string = '§'.join(result_pairs)
    print(result_string)

    # Open the file in write mode and write the updated dictionary
    with open(file_path, 'w') as file:
        # Write the result pairs with '§' between each pair
        file.write('§'.join(result_pairs))