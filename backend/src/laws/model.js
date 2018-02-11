import mongoose, { Schema } from 'mongoose';
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
    affects : {
        type: [String],
        enum: [
            'PROFESSIONAL',
            'RECREATIONAL',
            'DRAGGER',
            'FENCE',
            'NET',
            'FLY',
            'TRAP',
            'CORAL',
            'HOOK'
        ]
    },
    timeWindow: {
        type: new Schema({
            start: Date,
            end: Date,
        })
    },
    affectsIn: {
        type: Number,
        default: 0
    }
});

const model = mongoose.model('law', lawSchema);
export default model;
