import mongoose from 'mongoose';
import uuid from 'node-uuid';

const lawSchema = new mongoose.Schema({
    id: {
        type: String,
        default: uuid.v1
    },
    title: {
        type: String,
        required: true
    },
    abstract: {
        type: String
    },
    resource: {
        type: String,
        required: true
    },
    affectations: {
        type: [String]
    }
});

const model = mongoose.model('law', lawSchema);
export default model;
