<div align="center">
  <img src="./vyn_logo.png" alt="Vyn Logo" width="180" />
  <h1>Vyn Programming Language</h1>
  <p><strong>A Modern, Expressive, and Concurrency-First Scripting Language</strong></p>

  [![Version](https://img.shields.io/badge/version-1.0.0--beta-blue.svg)](https://github.com/Abdelaziz1586/Vyn)
  [![License](https://img.shields.io/badge/license-MIT-green.svg)](https://github.com/Abdelaziz1586/Vyn)
  [![Java](https://img.shields.io/badge/built%20with-Java-orange.svg)](https://www.java.com/)
</div>

---

## ⚡ What is Vyn?

**Vyn** is a lightweight, statement-driven scripting language designed for developers who value readability and flexibility. It bridges the gap between simple scripting and robust application development with its unique **Blueprint** system and native **Concurrency** primitives.

Whether you're building a quick automation script or a networked service, Vyn provides the tools to do it elegantly.

## 🚀 Key Features

- **Eloquent Syntax**: Clean, human-readable keywords like `make`, `check`, and `cycle`.
- **Blueprints (OOP)**: A powerful object-oriented system with inheritance (`mimics`) and deterministic lifecycles (`build`/`demolish`).
- **Concurrency-First**: Native `split` blocks for effortless background task execution.
- **Built-in Networking**: First-class `fetch` support for interacting with modern APIs.
- **Robust Exception Handling**: Defensive programming with `attempt` and `recover`.
- **Hot-loading**: Modularize your code seamlessly with the `use` system.

---

## 📖 Language Guide

### 1. Variables and Constants
Vyn distinguishes between mutable variables and immutable constants. Note that properties in Blueprints must be declared before use.

```rust
~ Mutable variable
make age 21 
make age 22 ~ Reassignment

~ Immutable constant
lock SERVER_PORT 8080
```

### 2. Control Flow

#### Conditionals
```rust
check status == "online" do
    say "System is operational"
otherwise
    say "System is maintenance mode"
end
```

#### Loops
Vyn supports both range-based and condition-based loops.
```rust
~ Range loop
cycle i from 1 to 5 do
    say "Iteration: " + i
end

~ While loop
make active true
cycle while active do
    ~ ... logic
    make active false
    escape ~ Optional break
end
```

### 3. Blueprint (Object-Oriented)
Blueprints are the core of Vyn's data modeling. Use `me` to access the current instance.

```rust
blueprint Service do
    make name
    make status

    build takes name do
        make me.name name
        make me.status "IDLE"
    end

    task start do
        make me.status "RUNNING"
        say me.name + " is now " + me.status
    end

    demolish do
        say "Service " + me.name + " shut down."
    end
end

make api new Service("AuthAPI")
api.start()
```

### 4. Tasks (Functions)
Tasks can return values using the `reply` keyword.

```rust
task calculateBonus takes salary, performance do
    check performance > 0.8 do
        reply salary * 0.2
    end
    reply 0
end

make bonus calculateBonus(5000, 0.9)
say "Bonus: " + bonus
```

### 5. Native API (The Standard Library)

#### Data Structures
- **`List`**: A dynamic array.
- **`Map`**: A key-value store.
```rust
make items new List
items.add("Vyn")

make config new Map
config.put("theme", "dark")
```

#### System Functions
- `size(obj)`: Returns the length of a list or map.
- `time()`: Current system time in milliseconds.
- `random(min, max)`: Generates a random number.
- `sort(list)`: Sorts a list in place.
- `input(prompt)`: Captures user input from the console.

#### Networking & Data
- `fetch(url, config)`: Perform HTTP requests.
- `pack(obj)` / `unpack(json)`: Native JSON serialization/deserialization.
- `at(list, index)`: Safe element access.

---

## 🌐 Networking Example

Vyn makes interacting with APIs incredibly simple with built-in JSON and HTTP support.

```rust
task getUserData takes userId do
    make url "https://api.example.com/users/" + userId
    
    attempt do
        make response fetch(url)
        make user unpack(response)
        reply user
    recover error do
        say "Failed to fetch user: " + error
    end
end
```

## 🧵 Concurrency with `split`

Need to run a heavy task without blocking the main thread? Just `split` it!

```rust
say "Starting background worker..."

split do
    hold 2000 ~ Sleep for 2 seconds
    say "Background task complete!"
end

say "Main thread is still running!"
```

---

## 🛠 Installation & Usage

1. Clone the repository: `git clone https://github.com/Abdelaziz1586/Vyn.git`
2. Build the project using Maven: `mvn clean package`
3. Run your scripts:

```bash
java -jar target/vyn-1.0.0.jar path/to/your_script.vyn
```

---

## 🤝 Contributing

We love contributions! If you have an idea for a feature or found a bug, please open an issue or submit a pull request.

**Vyn** is built for the community, by the community. ❤️
