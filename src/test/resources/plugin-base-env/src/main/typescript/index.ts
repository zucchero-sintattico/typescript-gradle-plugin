class Person {
    constructor(public name: string, public age: number) {
    }
    toString() {
        return `${this.name} is ${this.age} years old`;
    }
}

let p = new Person("John", 42);
console.log(p.toString());
