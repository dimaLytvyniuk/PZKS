export class NetworkParsingException extends Error {
    constructor (message: string) {
        super(message);

        Object.setPrototypeOf(this, NetworkParsingException.prototype);
    }
}
