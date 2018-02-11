import LawModel from './model';

const resolvers = {
    Query: {
        laws: (root, {affects}) => {
            const filter = affects.length ? {affects: {$in: affects}} : {};
            return LawModel.find(filter);
        },
        law: (root, {id}) => {
            return LawModel.find((law) => law.id === id);
        }
    },
    Mutation: {
        addLaw: (root, params) => {
            const {title, abstract, resource, affects, timeWindow} = params;
            const law = new LawModel({
                id: undefined,
                title,
                abstract,
                resource,
                affects,
                timeWindow
            });
            return law.save();
        }
    }
};

export default resolvers;
