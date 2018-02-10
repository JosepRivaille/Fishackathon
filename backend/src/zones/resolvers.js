import ZoneModel from './model';

const resolvers = {
    Query: {
        zones: () => {
            return ZoneModel.find({});
        },
        zone: (root, {id}) => {
            return ZoneModel.find((law) => law.id === id);
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
