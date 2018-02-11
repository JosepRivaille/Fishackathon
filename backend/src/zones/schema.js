import { makeExecutableSchema } from 'graphql-tools';

import resolvers from './resolvers';

const typeDefs = `
    type Zone {
        id: String
        code: String
        level: Level
        ocean: Ocean
        parent: String
        polygon: [[LatLng]]
        centroid: LatLng
        laws: [String]
    }
    
    type LatLng {
        lat: Float
        lng: Float
    }
    
    input LatLng2 {
        lat: Float
        lng: Float
    }
    
    enum Level {
        MAJOR
        SUBAREA
        DIVISION
        SUBDIVISION
        SUBUNIT
    }
    
    enum Ocean {
        ARTIC
        ATLANTIC
        PACIFIC
        INDIAN
        ANTARTIC
    }

    type Query {
        zones: [Zone]
    }
    
    type Mutation {
        addZone(
            code: String!,
            level: Level!,
            parent: String,
            ocean: Ocean!,
            polygon: [[LatLng2]]!,
            laws: [String]
        ): Zone
    }
`;

const schema = makeExecutableSchema({typeDefs, resolvers});
export default schema;
