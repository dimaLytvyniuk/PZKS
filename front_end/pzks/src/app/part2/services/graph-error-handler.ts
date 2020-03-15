import { ErrorHandler, Injectable } from '@angular/core';
import { NetworkParsingException } from '../errors/NetworkParsingException';

@Injectable()
export class GraphErrorHandler implements ErrorHandler {
    constructor () { }

    handleError(err) {
        if (err instanceof NetworkParsingException) {
            alert(err.message);
        } else {
            throw err;
        }
    }
}