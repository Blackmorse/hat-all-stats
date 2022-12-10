import { HttpError } from "./Https"

interface NotFoundError extends HttpError {
    entityType: string,
    entityId: string,
    description: string
}

export default NotFoundError