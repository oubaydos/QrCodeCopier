// FIXME
// although, this is not the worst idea, but the enum single source of truth should be backend
// so u gotta find a way to get the enum from backend to frontend ( maybe a script as this is a monorepo)
export enum MessageType {
    SESSION_ID = "SESSION_ID",
    REDIRECTION_URL= "REDIRECTION_URL",
    ERROR= "ERROR",
    CLOSING= "CLOSING"
}

export interface Message {
    type: MessageType,
    message: string
}