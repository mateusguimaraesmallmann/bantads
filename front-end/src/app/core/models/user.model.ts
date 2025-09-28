export interface User {
    name: string,
    email: string,
    cpf: string,
    password: string,
    role: string,
    status: "ACTIVE" | "DISABLED"
}
