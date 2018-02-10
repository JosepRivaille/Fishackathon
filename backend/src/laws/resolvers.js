import LawModel from './model';

const resolvers = {
    Query: {
        laws: () => {
            return LawModel.find({});
        },
        law: (root, {id}) => {
            return LawModel.find((law) => law.id === id);
        }
    },
    Mutation: {
        addLaw: (root, params) => {
            const { title, abstract, resource, affectations } = params;
            const law = new LawModel({
                id: undefined,
                title,
                abstract,
                resource,
                affectations
            });
            return law.save();
        }
    }
};

export default resolvers;
