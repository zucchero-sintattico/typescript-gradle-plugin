class Person {
    constructor(public name: string, public age: number) {
    }
    toString() {
        return `${this.name} is ${this.age} years old`;
    }
}
export { Person };