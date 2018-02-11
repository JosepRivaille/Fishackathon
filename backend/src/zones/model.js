import mongoose from 'mongoose';
import uuid from 'node-uuid';

const zoneSchema = new mongoose.Schema({
    id: {
        type: String,
        default: uuid.v1
    },
    code: {
        type: String,
        required: true
    },
    level: {
        type: String,
        required: true,
        enum: [
            'MAJOR',
            'SUBAREA',
            'DIVISION',
            'SUBDIVISION',
            'SUBUNIT'
        ],
    },
    parent: {
        type: String
    },
    ocean: {
        type: String,
        required: true,
        enum: [
            'ARTIC',
            'ATLANTIC',
            'PACIFIC',
            'INDIAN',
            'ANTARTIC'
        ]
    },
    polygon: {
        type: Array,
        required: true
    },
    centroid: {
        type: Object
    },
    laws: {
        type: [String]
    }
});

const model = mongoose.model('zone', zoneSchema);
export default model;
