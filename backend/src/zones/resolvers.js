import ZoneModel from './model';

const resolvers = {
    Query: {
        zones: ({lat, lng}) => {
            return ZoneModel.find({});
        }
    },
    Mutation: {
        addZone: (root, params) => {
            const { code, level, ocean, parent, polygon, laws } = params;
            const zone = new ZoneModel({
                id: undefined,
                code,
                level,
                ocean,
                parent,
                polygon,
                laws
            });
            return zone.save();
        }
    }
};

export default resolvers;
